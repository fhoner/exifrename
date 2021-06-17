package com.fhoner.exifrename.core.service;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.NonNull;

import java.util.Map;

public interface FileFormatter {

    String format(String value, Map<String, Tag> tags);

    String formatFilename(String scheme, @NonNull Metadata exifData);

}
