package com.fhoner.test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.model.GpsRecord;
import com.fhoner.exifrename.util.MetadataUtil;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestMetadataUtil {

    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";

    private static final String TAG_GPS_LAT_REF = "GPS/GPS Latitude Ref";
    private static final String TAG_GPS_LAT = "GPS/GPS Latitude";
    private static final String TAG_GPS_LONG_REF = "GPS/GPS Longitude Ref";
    private static final String TAG_GPS_LONG = "GPS/GPS Longitude";

    @Test
    public void shouldCorrectlyReadGpsTag() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME).getFile());
        Metadata exif = ImageMetadataReader.readMetadata(file);
        Map<String, Tag> tags = MetadataUtil.getTags(exif);

        assertThat(tags.get(TAG_GPS_LAT_REF), notNullValue());
        assertThat(tags.get(TAG_GPS_LAT), notNullValue());
        assertThat(tags.get(TAG_GPS_LONG_REF), notNullValue());
        assertThat(tags.get(TAG_GPS_LONG), notNullValue());

        String latStr = tags.get(TAG_GPS_LAT_REF).getDescription() + " " + tags.get(TAG_GPS_LAT).getDescription();
        String longStr = tags.get(TAG_GPS_LONG_REF).getDescription() + " " + tags.get(TAG_GPS_LONG).getDescription();
        GpsRecord lat = GpsRecord.parseString(latStr);
        GpsRecord lon = GpsRecord.parseString(longStr);
    }

    @Test
    public void shouldCorrectlyCalculatePositive() {
        GpsRecord rec = GpsRecord.builder()
                .ref(GpsRecord.Ref.N)
                .degrees(32)
                .minutes(18)
                .seconds(23.1)
                .build();
        assertThat(MetadataUtil.convertGpsToDecimalDegree(rec), closeTo(32.30642, 0.00001));
    }

    @Test
    public void shouldCorrectlyCalculateNegative() {
        GpsRecord rec = GpsRecord.builder()
                .ref(GpsRecord.Ref.W)
                .degrees(122)
                .minutes(36)
                .seconds(52.5)
                .build();
        assertThat(MetadataUtil.convertGpsToDecimalDegree(rec), closeTo(-122.61458, 0.00001));
    }

}
