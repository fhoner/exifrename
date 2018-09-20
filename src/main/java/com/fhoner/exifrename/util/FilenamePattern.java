package com.fhoner.exifrename.util;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.model.Address;
import com.fhoner.exifrename.model.GpsRecord;
import com.fhoner.exifrename.service.GeoService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FilenamePattern {

    private static final String TAG_GPS_LAT = "GPS Latitude";
    private static final String TAG_GPS_LONG = "GPS Longitude";

    private static final String CITY = "%c";
    private static final String PATTERN_DATETIME = "\\%d[(.*)+]";

    private static GeoService geoService = new GeoService();

    private String pattern;

    public static FilenamePattern fromString(@NonNull String str) {
        return new FilenamePattern(str);
    }

    public String formatFilename(@NonNull Metadata exifData) throws Exception {
        Map<String, Tag> tags = MetadataUtil.getTags(exifData);

        if (pattern.contains(CITY)) {
            insertAddress(tags);
        }

        return pattern;
    }

    private void insertAddress(Map<String, Tag> tags) throws Exception {
        GpsRecord lat = MetadataUtil.getLatitude(tags);
        GpsRecord lon = MetadataUtil.getLongtitude(tags);
        Address addr = geoService.reverseLookup(lat, lon);
        pattern = pattern.replace(CITY, addr.getAddress().getVillage());
    }

}
