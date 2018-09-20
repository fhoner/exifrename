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

/**
 * Class which provides functions to add files within directorys. After that, copy all files with new name into a
 * separate destination folder.
 */
public class FileService {

    private static final Set<String> FILE_EXTENSION_WHITELIST;

    static {
        FILE_EXTENSION_WHITELIST = new HashSet<>();
        FILE_EXTENSION_WHITELIST.add("jpg");
        FILE_EXTENSION_WHITELIST.add("jpeg");
    }

    private List<File> files = new ArrayList<>();

    /**
     * Adds files in the given directory. Only jpg/jpeg are considered, case insensitive.
     *
     * @param directory Directory to search for files.
     * @throws FileNotFoundException Thrown when given directory does not exist.
     */
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

    /**
     * Renames all found files previously by copying them into destination folder.
     *
     * @param pattern     The pattern for ne new file names.
     * @param destination Destination directory where files will be stored.
     * @throws Exception Thrown on several errors (tbd).
     */
    public void renameFiles(@NonNull FilenamePattern pattern, @NonNull String destination) throws Exception {
        if (destination.charAt(destination.length() - 1) != '/') {
            destination = destination + "/";
        }

        for (File file : files) {
            Metadata exif = ImageMetadataReader.readMetadata(file);
            Path source = Paths.get(file.getAbsolutePath());
            if (!Files.exists(Paths.get(destination))) {
                (new File(destination)).mkdirs();
            }
            Path destinationPath = Paths.get(destination + pattern.formatFilename(exif) + "." + FilenameUtils.getExtension(file.getName()));
            Files.copy(source, destinationPath);
        }
    }

}
