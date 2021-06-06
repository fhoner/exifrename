package com.fhoner.exifrename.core.tagging;

import com.adobe.internal.xmp.*;
import com.adobe.internal.xmp.options.SerializeOptions;
import com.icafe4j.image.meta.Metadata;
import com.icafe4j.image.meta.iptc.IPTCDataSet;
import com.icafe4j.image.meta.xmp.XMP;
import lombok.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Getter()
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMetaTagger {

    private static final String TEMP_DIR = System.getProperty("user.home");

    private File file;
    private XMPMeta xmp;
    private List<IPTCDataSet> iptc = new ArrayList<>();

    public static ImageMetaTagger fromFile(File file) throws IOException {
        ImageMetaTagger tagger = new ImageMetaTagger();
        FileInputStream fin = new FileInputStream(file);
        tagger.file = file;
        tagger.xmp = XmpParser.extractXMPMeta(fin);
        fin.close();
        return tagger;
    }

    public void writeFile(File destination) throws IOException, XMPException {
        String tempDest = TEMP_DIR + "/" + UUID.randomUUID();

        FileInputStream fin = new FileInputStream(file);
        FileOutputStream fout = new FileOutputStream(tempDest);
        String strMeta = XMPMetaFactory.serializeToString(xmp, new SerializeOptions().setOmitPacketWrapper(true));
        XMP.insertXMP(fin, fout, strMeta);
        fin.close();
        fout.close();

        fin = new FileInputStream(tempDest);
        fout = new FileOutputStream(destination);
        Metadata.insertIPTC(fin, fout, iptc);
        fin.close();
        fout.close();

        Files.delete(Paths.get(tempDest));
    }

    public ImageMetaTagger setPropertyNullSafe(@NonNull String ns, @NonNull String property, String value) throws XMPException {
        if (value != null) {
            xmp.setProperty(ns, property, value);
        }
        return this;
    }

    public XMPMeta getXmp() {
        return xmp;
    }

    public List<IPTCDataSet> getIptc() {
        return iptc;
    }

}
