package com.fhoner.exifrename.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.fhoner.exifrename.util.FilenamePattern;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileService {

    private static final Set<String> FILE_EXTENSION_WHITELIST;

    static {
        FILE_EXTENSION_WHITELIST = new HashSet<>();
        FILE_EXTENSION_WHITELIST.add("jpg");
        FILE_EXTENSION_WHITELIST.add("jpeg");
    }

    private List<File> files = new ArrayList<>();

    public void addFiles(@NonNull String directory) throws FileNotFoundException {
        File source = new File(directory);
        if (!source.exists()) {
            throw new FileNotFoundException();
        }

        File[] files = source.listFiles((dir, name) -> {
            String extension = FilenameUtils.getExtension(name).toLowerCase();
            return FILE_EXTENSION_WHITELIST.contains(extension);
        });
        this.files.addAll(Arrays.asList(files));
    }

    public void renameFiles(@NonNull FilenamePattern pattern, @NonNull String directory) throws Exception {
        if (directory.charAt(directory.length() - 1) != '/') {
            directory = directory + "/";
        }

        for (File file : files) {
            Metadata exif = ImageMetadataReader.readMetadata(file);
            Path source = Paths.get(file.getAbsolutePath());
            if (!Files.exists(Paths.get(directory))) {
                (new File(directory)).mkdirs();
            }
            Path destination = Paths.get(directory + pattern.formatFilename(exif) + "." + FilenameUtils.getExtension(file.getName()));
            Files.copy(source, destination);
        }
    }

}
