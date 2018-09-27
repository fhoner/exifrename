package com.fhoner.test.example;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.SerializeOptions;
import com.drew.imaging.ImageProcessingException;
import com.fhoner.exifrename.core.service.FileService;
import com.fhoner.exifrename.core.tagging.XmpParser;
import com.fhoner.exifrename.core.util.FilenamePattern;
import com.icafe4j.image.meta.Metadata;
import com.icafe4j.image.meta.iptc.IPTCApplicationTag;
import com.icafe4j.image.meta.iptc.IPTCDataSet;
import com.icafe4j.image.meta.xmp.XMP;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class TestSamples {

    @Test
    @Ignore
    public void sampleRenaming() {
        String source = "/home/fhoner/Desktop/source";
        String dest = "/home/fhoner/Desktop/dest";
        FilenamePattern pattern = FilenamePattern.fromString("%y-%m-%d-%h-%M-%s Motorrad %r %t");

        try {
            FileService service = new FileService();
            service.addFiles(source);
            service.createFiles(pattern, dest);
            service.getErrors().forEach((k, arr) -> {
                for (Exception ex : arr) {
                    System.out.print(k);
                    System.out.println("    > " + ex.getMessage());
                }
            });
        } catch (FileNotFoundException ex) {
            log.error("directory not found", ex);
        } catch (ImageProcessingException ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
        } catch (IOException ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
        }
    }

    @Test
    public void asd() throws Exception {
        FileInputStream fin = new FileInputStream("/home/fhoner/Desktop/after.jpg");
        XMPMeta meta = XmpParser.extractXMPMeta(fin);
        fin.close();

        meta.deleteProperty(XMPConst.NS_DC, "subject");
        meta.setProperty(XMPConst.NS_IPTCCORE, "CountryCode", "GER");
        meta.setProperty(XMPConst.NS_IPTCCORE, "Location", "Baden-Württemberg");
        meta.setProperty(XMPConst.NS_PHOTOSHOP, "City", "Gäufelden-Öschelbronn");
        meta.setProperty(XMPConst.NS_PHOTOSHOP, "Country", "Deutschland");
        meta.setProperty(XMPConst.NS_PHOTOSHOP, "State", "Baden-Württemberg");
        String strMeta = XMPMetaFactory.serializeToString(meta, new SerializeOptions().setOmitPacketWrapper(true));

        fin = new FileInputStream("/home/fhoner/Desktop/after.jpg");
        FileOutputStream fout = new FileOutputStream("/home/fhoner/Desktop/after_iptc.jpg");
        Metadata.insertIPTC(fin, fout, createIPTCDataSet());

        fin = new FileInputStream("/home/fhoner/Desktop/after_iptc.jpg");
        fout = new FileOutputStream("/home/fhoner/Desktop/after_iptc_xmp.jpg");
        XMP.insertXMP(fin, fout, strMeta);
    }

    private static List<IPTCDataSet> createIPTCDataSet() {
        List<IPTCDataSet> iptcs = new ArrayList<IPTCDataSet>();
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.COPYRIGHT_NOTICE, "Copyright 2018, Felix Honer"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.KEY_WORDS, "renameByExif"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.CONTENT_LOCATION_NAME, "Gäufelden-Öschelbronn"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.CONTENT_LOCATION_CODE, "BW"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.SUB_LOCATION, "Gäufelden"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.CITY, "Gäufelden-Öschelbronn"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.PROVINCE_STATE, "Baden-Württemberg"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.COUNTRY_CODE, "GER"));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.COUNTRY_NAME, "Deutschland"));
        return iptcs;
    }

}
