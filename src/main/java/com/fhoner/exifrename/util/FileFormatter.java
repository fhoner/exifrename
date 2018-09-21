package com.fhoner.exifrename.util;

import com.drew.metadata.Tag;
import com.fhoner.exifrename.model.GpsRecord;
import com.fhoner.exifrename.model.OSMRecord;
import com.fhoner.exifrename.service.GeoService;
import lombok.extern.log4j.Log4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j
public class FileFormatter {

    private static final String VILLAGE = "%v";
    private static final String COUNTY = "%c";
    private static final String STATE = "%S";
    private static final String COUNTRY = "%C";
    private static final List<String> LOCATION_DATA = Arrays.asList(new String[]{VILLAGE, COUNTY, STATE, COUNTRY});

    private static final String YEAR_FOUR = "%y";
    private static final String YEAR_TWO = "%Y";
    private static final String MONTH = "%m";
    private static final String DAY = "%d";
    private static final String HOUR = "%h";
    private static final String MINUTE = "%M";
    private static final String SECOND = "%s";

    private GeoService geoService = new GeoService();
    private Map<String, Tag> tags;
    private String value;

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
    public String format() throws Exception {
        insertLocationData();
        insertDateTime();
        return value;
    }

    private void insertLocationData() throws Exception {
        if (hasLocation()) {
            log.debug("location information needed");
            OSMRecord addr = getAddress();
            value = value.replace(VILLAGE, addr.getAddress().getVillage());
            value = value.replace(COUNTY, addr.getAddress().getCounty());
            value = value.replace(STATE, addr.getAddress().getState());
            value = value.replace(COUNTRY, addr.getAddress().getCountry());
        } else {
            log.debug("no location information needed; skip API call");
        }
    }

    private OSMRecord getAddress() throws Exception {
        GpsRecord lat = MetadataUtil.getLatitude(tags);
        GpsRecord lon = MetadataUtil.getLongtitude(tags);
        return geoService.reverseLookup(lat, lon);
    }

    private void insertDateTime() {
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

    private String formatDateTime(String pattern, LocalDateTime instance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(instance);
    }

}
