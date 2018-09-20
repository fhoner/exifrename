package com.fhoner.exifrename.util;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.model.GpsRecord;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MetadataUtil {

    private static final String TAG_GPS_LAT_REF = "GPS/GPS Latitude Ref";
    private static final String TAG_GPS_LAT = "GPS/GPS Latitude";
    private static final String TAG_GPS_LONG_REF = "GPS/GPS Longitude Ref";
    private static final String TAG_GPS_LONG = "GPS/GPS Longitude";

    private MetadataUtil() {
    }

    public static Map<String, Tag> getTags(Metadata exifData) {
        return StreamSupport.stream(exifData.getDirectories().spliterator(), false)
                .map(Directory::getTags)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(MetadataUtil::getKey, Function.identity()));
    }

    public static double convertGpsToDecimalDegree(GpsRecord rec) {
        int factor = rec.getRef() == GpsRecord.Ref.N ? 1 : -1;
        double val = factor * (Math.abs(rec.getDegrees()) + (rec.getMinutes() / 60.0) + (rec.getSeconds() / 3600.0));
        return val;
    }

    public static GpsRecord getLatitude(Map<String, Tag> tags) {
        String latStr = tags.get(TAG_GPS_LAT_REF).getDescription() + " " + tags.get(TAG_GPS_LAT).getDescription();
        return GpsRecord.parseString(latStr);
    }

    public static GpsRecord getLongtitude(Map<String, Tag> tags) {
        String longStr = tags.get(TAG_GPS_LONG_REF).getDescription() + " " + tags.get(TAG_GPS_LONG).getDescription();
        return GpsRecord.parseString(longStr);
    }

    private static String getKey(Tag tag) {
        return tag.getDirectoryName() + "/" + tag.getTagName();
    }

}
