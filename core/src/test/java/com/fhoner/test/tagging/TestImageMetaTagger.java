package com.fhoner.test.tagging;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPException;
import com.fhoner.exifrename.core.tagging.ImageMetaTagger;
import com.fhoner.exifrename.core.tagging.IptcTagSet;
import lombok.extern.log4j.Log4j;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j
public class TestImageMetaTagger {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String SAMPLE_IMAGE_NAME = "images/sample2.jpg";

    private static List<String> createdTempFiles = new ArrayList<>();

    private ImageMetaTagger tagger;
    private File fileBefore;

    @Before
    public void init() throws IOException {
        fileBefore = new File(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME).getFile());
        tagger = ImageMetaTagger.fromFile(fileBefore);
    }

    @Test
    public void shouldSetIptcWithoutError() throws XMPException, IOException {
        IptcTagSet allIptcTags = IptcTagSet.builder()
                .copyrightNotice("(c) exifrename")
                .keyWords("junit")
                .locationName("Stuttgart")
                .locationCode("Stgt")
                .subLocation("BW")
                .city("Stuttgart")
                .provinceState("Baden-Württemberg")
                .countryCode("GER")
                .countryName("Germany")
                .build();
        tagger.getIptc().addAll(allIptcTags.collect());
        String newFilename = TEMP_DIR + "/" + UUID.randomUUID() + ".jpg";
        createdTempFiles.add(newFilename);
        tagger.writeFile(new File(newFilename));
    }

    @Test
    public void shouldSetXmpWithoutError() throws XMPException, IOException {
        tagger.getXmp().deleteProperty(XMPConst.NS_DC, "subject");
        tagger.getXmp().setProperty(XMPConst.NS_IPTCCORE, "CountryCode", "GER");
        tagger.getXmp().setProperty(XMPConst.NS_IPTCCORE, "Location", "Baden-Württemberg");
        tagger.getXmp().setProperty(XMPConst.NS_PHOTOSHOP, "City", "Gäufelden-Öschelbronn");
        tagger.getXmp().setProperty(XMPConst.NS_PHOTOSHOP, "Country", "Deutschland");
        tagger.getXmp().setProperty(XMPConst.NS_PHOTOSHOP, "State", "Baden-Württemberg");
        String newFilename = TEMP_DIR + "/" + UUID.randomUUID() + ".jpg";
        createdTempFiles.add(newFilename);
        tagger.writeFile(new File(newFilename));
    }

    @Test
    public void shouldSetXmpAndIptcWithoutError() throws XMPException, IOException {
        IptcTagSet allIptcTags = IptcTagSet.builder()
                .copyrightNotice("(c) exifrename")
                .keyWords("junit")
                .locationName("Stuttgart")
                .locationCode("Stgt")
                .subLocation("BW")
                .city("Stuttgart")
                .provinceState("Baden-Württemberg")
                .countryCode("GER")
                .countryName("Germany")
                .build();
        tagger.getIptc().addAll(allIptcTags.collect());

        tagger.getXmp().deleteProperty(XMPConst.NS_DC, "subject");
        tagger.getXmp().setProperty(XMPConst.NS_IPTCCORE, "CountryCode", "GER");
        tagger.getXmp().setProperty(XMPConst.NS_IPTCCORE, "Location", "Baden-Württemberg");
        tagger.getXmp().setProperty(XMPConst.NS_PHOTOSHOP, "City", "Gäufelden-Öschelbronn");
        tagger.getXmp().setProperty(XMPConst.NS_PHOTOSHOP, "Country", "Deutschland");
        tagger.getXmp().setProperty(XMPConst.NS_PHOTOSHOP, "State", "Baden-Württemberg");

        String newFilename = TEMP_DIR + "/" + UUID.randomUUID() + ".jpg";
        createdTempFiles.add(newFilename);
        tagger.writeFile(new File(newFilename));
    }

    @AfterClass
    public static void cleanup() {
        createdTempFiles.forEach(file -> {
            if (Files.exists(Paths.get(file))) {
                try {
                    Files.delete(Paths.get(file));
                } catch (Exception ex) {
                    log.error("could not cleanup test data", ex);
                }
            }
        });
    }

}
