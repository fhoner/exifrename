package com.fhoner.test.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.service.GeoServiceImpl;
import com.fhoner.exifrename.core.util.MetadataUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.junit.MockServerRule;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;

public class TestGeoService {

    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";
    private static final String SAMPLE_OSM_RESPONSE = "mappings/osm.json";

    private static final String TAG_GPS_LAT_REF = "GPS/GPS Latitude Ref";
    private static final String TAG_GPS_LAT = "GPS/GPS Latitude";
    private static final String TAG_GPS_LONG_REF = "GPS/GPS Longitude Ref";
    private static final String TAG_GPS_LONG = "GPS/GPS Longitude";

    private GeoServiceImpl geoService;

    private String latStr;
    private String longStr;
    private GpsRecord lat;
    private GpsRecord lon;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    @Before
    public void init() throws Exception {
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME)).getFile());
        Metadata exif = ImageMetadataReader.readMetadata(file);
        Map<String, Tag> tags = MetadataUtil.getTags(exif);
        this.geoService = new GeoServiceImpl("http://127.0.0.1:" + mockServerRule.getPort() + "/osm/reverse?format=json&lat=${latitude}&lon=${longtitude}&addressdetails=1");
        latStr = tags.get(TAG_GPS_LAT_REF).getDescription() + " " + tags.get(TAG_GPS_LAT).getDescription();
        longStr = tags.get(TAG_GPS_LONG_REF).getDescription() + " " + tags.get(TAG_GPS_LONG).getDescription();
        lat = GpsRecord.parseString(latStr);
        lon = GpsRecord.parseString(longStr);
    }

    @Test
    public void shouldCorrectlyReverseLookup() throws Exception {
        File osmResponse = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(SAMPLE_OSM_RESPONSE)).getFile());
        mockServerRule.getClient().when(
                request()
                        .withMethod("GET")
                        .withPath("/osm/reverse")
                        .withQueryStringParameters(
                                param("format", "json"),
                                param("lon", "-1.4480916666666668"),
                                param("lat", "43.65600555555555"),
                                param("addressdetails", "1"))
        )
                .respond(
                        response().withBody(Files.readString(osmResponse.toPath())));

        OSMRecord result = geoService.reverseLookup(lat, lon);
        assertThat(result, notNullValue());
        assertThat(result.getPlaceId(), is(119579814L));
        assertThat(result.getOsmType(), is("way"));
        assertThat(result.getOsmId(), is(124414750L));
        assertThat(result.getLat(), is(43.6557748));
        assertThat(result.getLon(), is(-1.446497155742383));
        assertThat(result.getDisplayName(), is("Digue Nord, Quai du Vieil Adour, Le Port, Capbreton, District of Dax, Landes, New Aquitaine, Metropolitan France, 40130, France"));
        assertThat(result.getAddress().getWater(), nullValue());
        assertThat(result.getAddress().getFootway(), nullValue());
        assertThat(result.getAddress().getNeighbourhood(), is("Le Port"));
        assertThat(result.getAddress().getCounty(), is("Landes"));
        assertThat(result.getAddress().getState(), is("New Aquitaine"));
        assertThat(result.getAddress().getVillage(), is("Capbreton"));
        assertThat(result.getAddress().getCountry(), is("France"));
        assertThat(result.getAddress().getPostcode(), is("40130"));
        assertThat(result.getAddress().getCountryCode(), is("FR"));
    }

    //    @Test
//    public void shouldWaitBetweenCalls() throws Exception {
//        long first = System.currentTimeMillis();
//        geoService.clearCache();
//        geoService.reverseLookup(lat, lon);
//        geoService.clearCache();
//        geoService.reverseLookup(lat, lon);
//        long second = System.currentTimeMillis();
//        long diff = second - first;
//        assertThat(diff, greaterThan(1000L));
//    }
//
    @Test(expected = GpsReverseLookupException.class)
    public void shouldThrowOnHttpError() throws Exception {
        mockServerRule.getClient().when(
                request()
                        .withMethod("GET")
                        .withPath("/osm/reverse")
                        .withQueryStringParameters(
                                param("format", "json"),
                                param("lon", "-1.4480916666666668"),
                                param("lat", "43.65600555555555"),
                                param("addressdetails", "1"))
        )
                .respond(
                        response().withStatusCode(500));
        geoService.reverseLookup(lat, lon);
    }

}
