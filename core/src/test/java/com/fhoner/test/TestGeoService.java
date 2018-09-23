package com.fhoner.test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.service.GeoService;
import com.fhoner.exifrename.core.util.MetadataUtil;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class TestGeoService {

    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";

    private static final String TAG_GPS_LAT_REF = "GPS/GPS Latitude Ref";
    private static final String TAG_GPS_LAT = "GPS/GPS Latitude";
    private static final String TAG_GPS_LONG_REF = "GPS/GPS Longitude Ref";
    private static final String TAG_GPS_LONG = "GPS/GPS Longitude";

    private GeoService geoService;
    private GpsRecord lat;
    private GpsRecord lon;

    @Before
    public void init() throws Exception {
        geoService = new GeoService();

        File file = new File(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME).getFile());
        Metadata exif = ImageMetadataReader.readMetadata(file);
        Map<String, Tag> tags = MetadataUtil.getTags(exif);
        String latStr = tags.get(TAG_GPS_LAT_REF).getDescription() + " " + tags.get(TAG_GPS_LAT).getDescription();
        String longStr = tags.get(TAG_GPS_LONG_REF).getDescription() + " " + tags.get(TAG_GPS_LONG).getDescription();
        lat = GpsRecord.parseString(latStr);
        lon = GpsRecord.parseString(longStr);

        OSMRecord result = geoService.reverseLookup(lat, lon);
        assertThat(result, notNullValue());
        assertThat(result.getAddress().getVillage(), is("Capbreton"));
    }

    @Test
    public void shouldCorrectlyReverseLookup() throws Exception {
        geoService.reverseLookup(lat, lon);
    }

    @Test(expected = GpsReverseLookupException.class)
    public void shouldThrowOnHttpError() throws Exception {
        GeoService mock = new GeoServiceMock();
        mock.reverseLookup(lat, lon);
    }

    private class GeoServiceMock extends GeoService {
        @Override
        protected Response makeHttpRequest(GpsRecord lat, GpsRecord lon) {
            return Response.serverError().build();
        }
    }

}
