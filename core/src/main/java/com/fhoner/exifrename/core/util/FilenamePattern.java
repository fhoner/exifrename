package com.fhoner.exifrename.core.util;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * Used to represent a schema for naming.
 */
@Getter
@Log4j2
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
        try {
            Map<String, Tag> tags = MetadataUtil.getTags(exifData);
            FileFormatter formatter = new FileFormatter(this.pattern, tags);
            String formatted = formatter.format();
            this.errors.addAll(formatter.getErrors());
            return formatted.replace("/", "-");
        } catch(Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

}
