package com.fhoner.exifrename.core.service;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.exception.TagEmptyException;
import com.fhoner.exifrename.core.exception.TagNotFoundException;
import com.fhoner.exifrename.core.model.Address;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.util.MetadataUtil;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.TextStringBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Getter
public class FileFormatterImpl implements FileFormatter {

    private static final String UNKNOWN_DATETIME = "[No-Date]";
    private static final String UNKNOWN_GPS = "[No-GPS]";

    private static final List<String> SEPARATOR_CHARS = Arrays.asList(" ", ",", "-");
    private static final String DEFAULT_SEPARATOR = " ";

    private static final String TOWN = "%t";
    private static final String COUNTY = "%c";
    private static final String STATE = "%S";
    private static final String COUNTRY = "%C";
    private static final String COUNTRY_CODE = "%r";
    private static final List<String> LOCATION_DATA = Arrays.asList(TOWN, COUNTY, STATE, COUNTRY, COUNTRY_CODE);

    private static final String YEAR_FOUR = "%y";
    private static final String YEAR_TWO = "%Y";
    private static final String MONTH = "%m";
    private static final String DAY = "%d";
    private static final String HOUR = "%h";
    private static final String MINUTE = "%M";
    private static final String SECOND = "%s";
    private static final List<String> DATETIME_DATA = Arrays.asList(YEAR_FOUR, YEAR_TWO, MONTH, DAY, HOUR, MINUTE, SECOND);

    private final GeoService geoService;
    private final AddressService addressService;

    @Inject
    public FileFormatterImpl(GeoService geoService, AddressService addressService) {
        this.geoService = geoService;
        this.addressService = addressService;
    }

    /**
     * Formats the file with the given schema.
     *
     * @return Formatted string.
     * @throws Exception
     */
    public String format(String value, Map<String, Tag> tags) {
        try {
            value = insertLocationData(value, tags);
        } catch (TagEmptyException | GpsReverseLookupException ex) {
            log.error(ex);
        }

        try {
            value = insertDateTime(value, tags);
        } catch (TagNotFoundException ex) {
            log.error(ex);
        }

        value = replaceVariables(value, LOCATION_DATA, UNKNOWN_GPS);
        value = replaceVariables(value, DATETIME_DATA, UNKNOWN_DATETIME);
        value = postFormat(value);
        return value;
    }

    private String insertLocationData(String value, Map<String, Tag> tags)
            throws TagEmptyException, GpsReverseLookupException {
        if (hasLocation(value)) {
            log.debug("location information needed");
            OSMRecord osmrec = addressService.getAddress(tags);
            Address address = osmrec.getAddress();
            value = insertValueNullsafe(value, TOWN, address.getVillage());
            value = insertValueNullsafe(value, COUNTY, address.getCounty());
            value = insertValueNullsafe(value, STATE, address.getState());
            value = insertValueNullsafe(value, COUNTRY, address.getCountry());
            value = insertValueNullsafe(value, COUNTRY_CODE, address.getCountryCode());
            return value;
        } else {
            log.debug("no location information needed; skip API call");
        }
        return value;
    }

    private String insertValueNullsafe(String value, String variable, String content) {
        if (content != null) {
            return value.replace(variable, content);
        } else {
            log.info("information not available for " + variable);
            return value;
        }
    }

    private String insertDateTime(String value, Map<String, Tag> tags) throws TagNotFoundException {
        LocalDateTime date = MetadataUtil.getDateTime(tags);
        value = value.replace(YEAR_FOUR, formatDateTime("yyyy", date));
        value = value.replace(YEAR_TWO, formatDateTime("yy", date));
        value = value.replace(MONTH, formatDateTime("MM", date));
        value = value.replace(DAY, formatDateTime("dd", date));
        value = value.replace(HOUR, formatDateTime("HH", date));
        value = value.replace(MINUTE, formatDateTime("mm", date));
        value = value.replace(SECOND, formatDateTime("ss", date));
        return value;
    }

    private boolean hasLocation(String value) {
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
    private String replaceVariables(String value, List<String> variables, String replacement) {
        boolean removed = false;
        for (String s : variables) {
            removed = removed | value.contains(s);
            value = value.replace(s, "");
        }
        if (removed) {
            value += replacement;
        }
        return value;
    }

    /**
     * Does the post formatting actions: first remove unnecessary separators, then remove whitespaces on begin and
     * end.
     */
    private String postFormat(String value) {
        String[] temp = new String[]{value};
        // replace double separators with a single one, e.g. "  [xy]" to " [xy]"
        SEPARATOR_CHARS.forEach(s -> {
            String pattern = Pattern.quote(s) + Pattern.quote(s) + "+";
            temp[0] = temp[0].replaceAll(pattern + Pattern.quote(UNKNOWN_GPS), s + UNKNOWN_GPS);
            temp[0] = temp[0].replaceAll(pattern + Pattern.quote(UNKNOWN_DATETIME), s + UNKNOWN_DATETIME);
        });
        value = temp[0];

        // add separator before last []-tag if no one exists, e.g. "asd[unknown]" to "asd [unknown]"
        String regex = ".*[^ ]((\\[No-Date\\]|\\[No-GPS\\]))$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String match = matcher.group(1);
            TextStringBuilder builder = new TextStringBuilder(value);
            int posBeforeMatch = value.length() - match.length();
            builder.insert(posBeforeMatch, DEFAULT_SEPARATOR);
            value = builder.build();
        }

        value = value.trim();
        return value;
    }

    /**
     * Gets a filename based on the schema and the given exif data.
     *
     * @param exifData Exif metadata from picture.
     * @return The formatted filename.
     * @throws Exception Thrown when values could not be extracted from tags.
     */
    public String formatFilename(String scheme, @NonNull Metadata exifData) {
        try {
            Map<String, Tag> tags = MetadataUtil.getTags(exifData);
            String formatted = format(scheme, tags);
            return formatted.replace("/", "-");
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

}
