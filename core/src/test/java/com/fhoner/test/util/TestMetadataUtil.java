package com.fhoner.test.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.TagEmptyException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.util.MetadataUtil;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Map;

import static com.fhoner.exifrename.core.util.MetadataUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestMetadataUtil {

    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";

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

    @Test(expected = TagEmptyException.class)
    public void shouldThrowOnLonNull() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME).getFile());
        Metadata exif = ImageMetadataReader.readMetadata(file);
        Map<String, Tag> tags = MetadataUtil.getTags(exif);
        tags.forEach((s, t) -> tags.put(s, null));
        MetadataUtil.getLongtitude(tags);
    }

    @Test
    public void shouldCorrectlyCalculatePositive() {
        GpsRecord rec = GpsRecord.builder()
                .ref(GpsRecord.Ref.N)
                .degrees(32)
                .minutes(new BigDecimal(18))
                .seconds(new BigDecimal(23.1))
                .build();
        assertThat(MetadataUtil.convertGpsToDecimalDegree(rec), closeTo(32.30642, 0.00001));
    }

    @Test
    public void shouldCorrectlyCalculateNegative() {
        GpsRecord rec = GpsRecord.builder()
                .ref(GpsRecord.Ref.W)
                .degrees(122)
                .minutes(new BigDecimal(36))
                .seconds(new BigDecimal(52.5))
                .build();
        assertThat(MetadataUtil.convertGpsToDecimalDegree(rec), closeTo(-122.61458, 0.00001));
    }

    @Test
    public void shouldHavePrivateConstructor() throws Exception {
        Constructor<MetadataUtil> constructor = MetadataUtil.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers()), is(true));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
