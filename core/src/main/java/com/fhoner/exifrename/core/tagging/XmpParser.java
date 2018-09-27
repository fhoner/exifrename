package com.fhoner.exifrename.core.tagging;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XmpParser {

    private static final int XMP_HEADER_SIZE = 29;
    private static final String XMP_HEADER = "http://ns.adobe.com/xap/1.0/\0";

    private static final int M_SOI = 0xd8; // File start marker.
    private static final int M_APP1 = 0xe1; // Marker for Exif or XMP.
    private static final int M_SOS = 0xda; // Image data marker.

    public static XMPMeta extractXMPMeta(InputStream is) {
        List<Section> sections = parse(is, true);
        if (sections == null) {
            return null;
        }
        // Now we don't support extended xmp.
        for (Section section : sections) {
            if (hasXMPHeader(section.data)) {
                int end = getXMPContentEnd(section.data);
                byte[] buffer = new byte[end - XMP_HEADER_SIZE];
                System.arraycopy(
                        section.data, XMP_HEADER_SIZE, buffer, 0, buffer.length);
                try {
                    XMPMeta result = XMPMetaFactory.parseFromBuffer(buffer);
                    return result;
                } catch (XMPException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private static List<Section> parse(InputStream is, boolean readMetaOnly) {
        try {
            if (is.read() != 0xff || is.read() != M_SOI) {
                return null;
            }
            List<Section> sections = new ArrayList<>();
            int c;
            while ((c = is.read()) != -1) {
                if (c != 0xff) {
                    return null;
                }
                // Skip padding bytes.
                while ((c = is.read()) == 0xff) {
                }
                if (c == -1) {
                    return null;
                }
                int marker = c;
                if (marker == M_SOS) {
                    // M_SOS indicates the image data will follow and no metadata after
                    // that, so read all data at one time.
                    if (!readMetaOnly) {
                        Section section = new Section();
                        section.marker = marker;
                        section.length = -1;
                        section.data = new byte[is.available()];
                        is.read(section.data, 0, section.data.length);
                        sections.add(section);
                    }
                    return sections;
                }
                int lh = is.read();
                int ll = is.read();
                if (lh == -1 || ll == -1) {
                    return null;
                }
                int length = lh << 8 | ll;
                if (!readMetaOnly || c == M_APP1) {
                    Section section = new Section();
                    section.marker = marker;
                    section.length = length;
                    section.data = new byte[length - 2];
                    is.read(section.data, 0, length - 2);
                    sections.add(section);
                } else {
                    // Skip this section since all exif/xmp meta will be in M_APP1
                    // section.
                    is.skip(length - 2);
                }
            }
            return sections;
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore.
                }
            }
        }
    }

    private static boolean hasXMPHeader(byte[] data) {
        if (data.length < XMP_HEADER_SIZE) {
            return false;
        }
        try {
            byte[] header = new byte[XMP_HEADER_SIZE];
            System.arraycopy(data, 0, header, 0, XMP_HEADER_SIZE);
            if (new String(header, "UTF-8").equals(XMP_HEADER)) {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return false;
    }

    private static int getXMPContentEnd(byte[] data) {
        for (int i = data.length - 1; i >= 1; --i) {
            if (data[i] == '>') {
                if (data[i - 1] != '?') {
                    return i + 1;
                }
            }
        }
        // It should not reach here for a valid xmp meta.
        return data.length;
    }

    private static class Section {
        public int marker;
        public int length;
        public byte[] data;
    }

}
