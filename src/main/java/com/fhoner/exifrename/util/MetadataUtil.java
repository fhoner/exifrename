package com.fhoner.exifrename.util;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.model.GpsRecord;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Provides some util methods.
 */
public class MetadataUtil {

    public static final String TAG_GPS_LAT_REF = "GPS/GPS Latitude Ref";
    public static final String TAG_GPS_LAT = "GPS/GPS Latitude";
    public static final String TAG_GPS_LONG_REF = "GPS/GPS Longitude Ref";
    public static final String TAG_GPS_LONG = "GPS/GPS Longitude";
    public static final String TAGS_DATE_TIME = "Exif IFD0/Date/Time";

    private static final String DATE_TIME_FORMAT = "yyyy:MM:dd HH:mm:ss";

    private MetadataUtil() {
    }

    /**
     * Gets a map of the tags with directoryname/tagname as key.
     *
     * @param exifData Picture metadata.
     * @return Filled map.
     */
    public static Map<String, Tag> getTags(@NonNull Metadata exifData) {
        return StreamSupport.stream(exifData.getDirectories().spliterator(), false)
                .map(Directory::getTags)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(MetadataUtil::getKey, Function.identity()));
    }

    /**
     * Converts a {@link GpsRecord} to degree-distance mode.
     *
     * @param rec Record to convert.
     * @return Converted value as double.
     */
    public static double convertGpsToDecimalDegree(@NonNull GpsRecord rec) {
        int factor = rec.getRef() == GpsRecord.Ref.N ? 1 : -1;
        double val = factor * (Math.abs(rec.getDegrees()) + (rec.getMinutes() / 60.0) + (rec.getSeconds() / 3600.0));
        return val;
    }

    /**
     * Extracts the latitude as a {@link GpsRecord} from exif tags.
     *
     * @param tags Exif metadata.
     * @return Extracted latitude.
     */
    public static GpsRecord getLatitude(@NonNull Map<String, Tag> tags) {
        String latStr = tags.get(TAG_GPS_LAT_REF).getDescription() + " " + tags.get(TAG_GPS_LAT).getDescription();
        return GpsRecord.parseString(latStr);
    }

    /**
     * Gets the creation date of a picture.
     *
     * @param tags Exif metadata.
     * @return Date and time of creation.
     */
    public static LocalDateTime getDateTime(@NonNull Map<String, Tag> tags) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return LocalDateTime.parse(tags.get(TAGS_DATE_TIME).getDescription(), formatter);
    }

    /**
     * Extracts the longtitude as a {@link GpsRecord} from exif tags.
     *
     * @param tags Exif metadata.
     * @return Extracted longtitude.
     */
    public static GpsRecord getLongtitude(@NonNull Map<String, Tag> tags) {
        String longStr = tags.get(TAG_GPS_LONG_REF).getDescription() + " " + tags.get(TAG_GPS_LONG).getDescription();
        return GpsRecord.parseString(longStr);
    }

    private static String getKey(Tag tag) {
        return tag.getDirectoryName() + "/" + tag.getTagName();
    }

}
