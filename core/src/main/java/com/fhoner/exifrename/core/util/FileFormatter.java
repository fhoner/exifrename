package com.fhoner.exifrename.core.util;

import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.exception.TagEmptyException;
import com.fhoner.exifrename.core.exception.TagNotFoundException;
import com.fhoner.exifrename.core.model.Address;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.service.GeoService;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.text.StrBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
@Getter
public class FileFormatter {

    private static final String UNKNOWN_DATETIME = "[No-Date]";
    private static final String UNKNOWN_GPS = "[No-GPS]";

    private static final List<String> SEPARATOR_CHARS = Arrays.asList(new String[]{" ", ",", "-"});
    private static final String DEFAULT_SEPARATOR = " ";

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
        } catch (TagEmptyException | GpsReverseLookupException ex) {
            replaceVariables(LOCATION_DATA, UNKNOWN_GPS);
            errors.add(ex);
        }

        try {
            insertDateTime();
        } catch (TagNotFoundException ex) {
            replaceVariables(DATETIME_DATA, UNKNOWN_DATETIME);
            errors.add(ex);
        }

        postFormat();
        return value;
    }

    private void insertLocationData() throws TagEmptyException, GpsReverseLookupException {
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

    private OSMRecord getAddress() throws TagEmptyException, GpsReverseLookupException {
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

    private String formatDateTime(String pattern, LocalDateTime instance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(instance);
    }

    /**
     * Removes all given variables. Value of replacement will be put to filename ending.
     *
     * @param variables
     * @param replacement
     */
    private void replaceVariables(List<String> variables, String replacement) {
        boolean removed = false;
        for (String s : variables) {
            removed = removed | value.contains(s);
            value = value.replace(s, "");
        }
        if (removed) {
            value += replacement;
        }
    }

    /**
     * Does the post formatting actions: first remove unnecessary separators, then remove whitespaces on begin and
     * end.
     */
    private void postFormat() {
        // replace double separators with a single one, e.g. "  [xy]" to " [xy]"
        SEPARATOR_CHARS.forEach(s -> {
            String pattern = Pattern.quote(s) + Pattern.quote(s) + "+";
            value = value.replaceAll(pattern + Pattern.quote(UNKNOWN_GPS), s + UNKNOWN_GPS);
            value = value.replaceAll(pattern + Pattern.quote(UNKNOWN_DATETIME), s + UNKNOWN_DATETIME);
        });

        // add separator before last []-tag if no one exists, e.g. "asd[unknown]" to "asd [unknown]"
        String regex = ".*[^ ]((\\[No-Date\\]|\\[No-GPS\\]))$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String match = matcher.group(1);
            StrBuilder builder = new StrBuilder(value);
            int posBeforeMatch = value.length() - match.length();
            builder.insert(posBeforeMatch, DEFAULT_SEPARATOR);
            value = builder.build();
        }

        value = value.trim();
    }

}
