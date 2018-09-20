package com.fhoner.exifrename.util;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fhoner.exifrename.service.GeoService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Map;

/**
 * Used to represent a schema for naming.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FilenamePattern {

    private String pattern;

    /**
     * Creates a new instance.
     *
     * @param str Schema for naming.
     * @return Instance of {@link FilenamePattern}.
     */
    public static FilenamePattern fromString(@NonNull String str) {
        return new FilenamePattern(str);
    }

    /**
     * Gets a filename based on the schema and the given exif data.
     *
     * @param exifData Exif metadata from picture.
     * @return The formatted filename.
     * @throws Exception Thrown when values could not be extracted from tags.
     */
    public String formatFilename(@NonNull Metadata exifData) throws Exception {
        Map<String, Tag> tags = MetadataUtil.getTags(exifData);
        FileFormatter formatter = new FileFormatter(this.pattern, tags);
        return formatter.format();
    }

}
