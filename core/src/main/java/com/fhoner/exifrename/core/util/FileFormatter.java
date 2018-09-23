package com.fhoner.exifrename.core.util;

import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.exception.TagNotFoundException;
import com.fhoner.exifrename.core.model.Address;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.service.GeoService;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Log4j
@Getter
public class FileFormatter {

    private static final String DATA_UNKNOWN_TEXT = "[unknown]";
    private static final String NO_GPS_DATA_AVAILABLE = "[No-GPS]";

    private static final String TOWN = "%t";
    private static final String COUNTY = "%c";
    private static final String STATE = "%S";
    private static final String COUNTRY = "%C";
    private static final String COUNTRY_CODE = "%r";
    private static final List<String> LOCATION_DATA = Arrays.asList(new String[]{TOWN, COUNTY, STATE, COUNTRY, COUNTRY_CODE});

    private static final String YEAR_FOUR = "%y";
    private static final String YEAR_TWO = "%Y";
    private static final String MONTH = "%m";
    private static final String DAY = "%d";
    private static final String HOUR = "%h";
    private static final String MINUTE = "%M";
    private static final String SECOND = "%s";
    private static final List<String> DATETIME_DATA = Arrays.asList(new String[]{YEAR_FOUR, YEAR_TWO, MONTH, DAY, HOUR, MINUTE, SECOND});

    private static GeoService geoService = new GeoService();

    private Map<String, Tag> tags;
    private String value;
    private List<Exception> errors = new ArrayList<>();

    public FileFormatter(String value, Map<String, Tag> tags) {
        this.value = value;
        this.tags = tags;
    }

    /**
     * Formats the file with the given schema.
     *
     * @return Formatted string.
     * @throws Exception
     */
    public String format() {
        try {
            insertLocationData();
        } catch (TagNotFoundException | GpsReverseLookupException ex) {
            removeLocationData();
            errors.add(ex);
        }

        try {
            insertDateTime();
        } catch (Exception ex) {
            removeVariables();
            errors.add(ex);
        }

        return value;
    }

    private void insertLocationData() throws TagNotFoundException, GpsReverseLookupException {
        if (hasLocation()) {
            log.debug("location information needed");
            OSMRecord osmrec = getAddress();
            Address address = osmrec.getAddress();
            String v = address.getVillage();
            insertValueNullsafe(TOWN, address.getVillage());
            insertValueNullsafe(COUNTY, address.getCounty());
            insertValueNullsafe(STATE, address.getState());
            insertValueNullsafe(COUNTRY, address.getCountry());
            insertValueNullsafe(COUNTRY_CODE, address.getCountryCode());
        } else {
            log.debug("no location information needed; skip API call");
        }
    }

    private void insertValueNullsafe(String variable, String content) {
        if (content != null) {
            this.value = this.value.replace(variable, content);
        } else {
            log.info("information not available for " + variable);
        }
    }

    private OSMRecord getAddress() throws TagNotFoundException, GpsReverseLookupException {
        GpsRecord lat = MetadataUtil.getLatitude(tags);
        GpsRecord lon = MetadataUtil.getLongtitude(tags);
        return geoService.reverseLookup(lat, lon);
    }

    private void insertDateTime() throws TagNotFoundException {
        LocalDateTime date = MetadataUtil.getDateTime(tags);
        value = value.replace(YEAR_FOUR, formatDateTime("yyyy", date));
        value = value.replace(YEAR_TWO, formatDateTime("yy", date));
        value = value.replace(MONTH, formatDateTime("MM", date));
        value = value.replace(DAY, formatDateTime("dd", date));
        value = value.replace(HOUR, formatDateTime("HH", date));
        value = value.replace(MINUTE, formatDateTime("mm", date));
        value = value.replace(SECOND, formatDateTime("ss", date));
    }

    private boolean hasLocation() {
        for (String s : LOCATION_DATA) {
            if (value.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private void removeVariables() {
        Stream.concat(LOCATION_DATA.stream(), DATETIME_DATA.stream())
                .forEach(s -> this.value = this.value.replace(s, DATA_UNKNOWN_TEXT));
    }

    private String formatDateTime(String pattern, LocalDateTime instance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(instance);
    }

    private void removeLocationData() {
        boolean removed = false;
        for (String s : LOCATION_DATA) {
            removed = true;
            this.value = this.value.replace(s, "");
        }
        if (removed) {
            this.value += NO_GPS_DATA_AVAILABLE;
        }
    }

}
