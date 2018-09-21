package com.fhoner.test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.model.Address;
import com.fhoner.exifrename.model.GpsRecord;
import com.fhoner.exifrename.model.OSMRecord;
import com.fhoner.exifrename.service.GeoService;
import com.fhoner.exifrename.util.FileFormatter;
import com.fhoner.exifrename.util.FilenamePattern;
import com.fhoner.exifrename.util.MetadataUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestFormatting {

    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";

    private Metadata exif;
    private Map<String, Tag> tags;
    private OSMRecord sampleOSMRecord;
    private GeoService geoServiceMock;

    @Before
    public void init() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME).getFile());
        exif = ImageMetadataReader.readMetadata(file);
        tags = MetadataUtil.getTags(exif);

        sampleOSMRecord = OSMRecord.builder().address(Address.builder()
                .neighbourhood("Le Port")
                .village("Capbreton")
                .county("Dax")
                .state("New Aquitaine")
                .country("France")
                .postcode("40130")
                .build()
        ).build();

        geoServiceMock = mock(GeoService.class);
        when(geoServiceMock.reverseLookup(any(GpsRecord.class), any(GpsRecord.class))).thenReturn(sampleOSMRecord);
    }

    @Test
    public void shouldFormatDateTime() throws Exception {
        FileFormatter formatter = new FileFormatter("%y-%m-%d %m:%h:%s", tags);
        Whitebox.setInternalState(formatter, "tags", tags);
        assertThat(formatter.format(), is("2018-08-07 08:20:35"));
    }

    @Test
    public void shouldFormatAddress() throws Exception {
        FileFormatter formatter = new FileFormatter("%v %c %S %C", tags);
        Whitebox.setInternalState(formatter, "tags", tags);
        Whitebox.setInternalState(formatter, "geoService", geoServiceMock);
        assertThat(formatter.format(), is("Capbreton Dax New Aquitaine France"));
    }

    @Test
    public void shouldFormatMixed() throws Exception {
        FileFormatter formatter = new FileFormatter("%v Felix %y-Honer %c %S %C", tags);
        Whitebox.setInternalState(formatter, "tags", tags);
        Whitebox.setInternalState(formatter, "geoService", geoServiceMock);
        assertThat(formatter.format(), is("Capbreton Felix 2018-Honer Dax New Aquitaine France"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterFromString() {
        FilenamePattern.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterOnFormat() throws Exception {
        FilenamePattern.fromString("").formatFilename(null);
    }

}