package com.fhoner.exifrename.core.util;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Used to represent a schema for naming.
 */
@Getter
public class FilenamePattern {

    private String pattern;
    private Set<Exception> errors = new HashSet<>();

    private FilenamePattern(String str) {
        this.pattern = str;
    }

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
    public String formatFilename(@NonNull Metadata exifData) {
        errors.clear();
        Map<String, Tag> tags = MetadataUtil.getTags(exifData);
        FileFormatter formatter = new FileFormatter(this.pattern, tags);
        String formatted = formatter.format();
        this.errors.addAll(formatter.getErrors());
        return formatted;
    }

}
