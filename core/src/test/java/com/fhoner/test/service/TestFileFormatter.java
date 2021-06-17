//package com.fhoner.test.service;
//
//import com.drew.imaging.ImageMetadataReader;
//import com.drew.metadata.Metadata;
//import com.drew.metadata.Tag;
//import com.fhoner.exifrename.core.model.*;
//import com.fhoner.exifrename.core.service.GeoService;
//import com.fhoner.exifrename.core.service.FileFormatterImpl;
//import com.fhoner.exifrename.core.util.MetadataUtil;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.internal.util.reflection.Whitebox;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.io.File;
//import java.util.Map;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class TestFileFormatter {
//
//    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";
//
//    private Metadata exif;
//    private Map<String, Tag> tags;
//    private OSMRecord sampleOSMRecord;
//    private GeoService geoServiceMock;
//
//    @Before
//    public void init() throws Exception {
//        File file = new File(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME).getFile());
//        exif = ImageMetadataReader.readMetadata(file);
//        tags = MetadataUtil.getTags(exif);
//
//        sampleOSMRecord = OSMRecord.builder().address(Address.builder()
//                .neighbourhood("Le Port")
//                .village("Capbreton")
//                .county("Dax")
//                .state("New Aquitaine")
//                .country("France")
//                .postcode("40130")
//                .build()
//        ).build();
//
//        geoServiceMock = mock(GeoService.class);
//        when(geoServiceMock.reverseLookup(any(GpsRecord.class), any(GpsRecord.class))).thenReturn(sampleOSMRecord);
//    }
//
//    @Test
//    public void shouldFormatDateTime() throws Exception {
//        FileFormatterImpl formatter = new FileFormatterImpl("%y-%m-%d %m:%h:%s", tags);
//        Whitebox.setInternalState(formatter, "tags", tags);
//        assertThat(formatter.format(), equalTo("2018-08-07 08:20:35"));
//    }
//
//    @Test
//    public void shouldFormatAddress() throws Exception {
//        FileFormatterImpl formatter = new FileFormatterImpl("%t %c %S %C", tags);
//        Whitebox.setInternalState(formatter, "tags", tags);
//        Whitebox.setInternalState(formatter, "geoService", geoServiceMock);
//        assertThat(formatter.format(), equalTo("Capbreton Landes Nouvelle-Aquitaine France"));
//    }
//
//    @Test
//    public void shouldFormatMixed() throws Exception {
//        FileFormatterImpl formatter = new FileFormatterImpl("%t Felix %y-Honer %c %S %C", tags);
//        Whitebox.setInternalState(formatter, "tags", tags);
//        Whitebox.setInternalState(formatter, "geoService", geoServiceMock);
//        assertThat(formatter.format(), equalTo("Capbreton Felix 2018-Honer Landes Nouvelle-Aquitaine France"));
//    }
//
//    @Test
//    public void shouldReplaceGpsWhenUnknown() {
//        tags.clear();
//        FileFormatterImpl formatter = new FileFormatterImpl("%t Felix Honer %c %S %C", tags);
//        Whitebox.setInternalState(formatter, "tags", tags);
//        Whitebox.setInternalState(formatter, "geoService", geoServiceMock);
//        assertThat(formatter.format(), equalTo("Felix Honer [No-GPS]"));
//    }
//
//    @Test
//    public void shouldReplaceDateWhenUnknown() {
//        tags.clear();
//        FileFormatterImpl formatter = new FileFormatterImpl("%y test", tags);
//        Whitebox.setInternalState(formatter, "tags", tags);
//        assertThat(formatter.format(), equalTo("test [No-Date]"));
//    }
//
//    @Test
//    public void shouldPlaceUnknownHintAtCorrectPosition() {
//        tags.clear();
//        FileFormatterImpl formatter = new FileFormatterImpl("%y %t Felix Honer %c %S %C", tags);
//        Whitebox.setInternalState(formatter, "tags", tags);
//        Whitebox.setInternalState(formatter, "geoService", geoServiceMock);
//        assertThat(formatter.format(), equalTo("Felix Honer [No-GPS] [No-Date]"));
//    }
//
//}
