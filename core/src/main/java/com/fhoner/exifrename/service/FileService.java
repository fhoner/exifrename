package com.fhoner.exifrename.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.fhoner.exifrename.model.FileServiceUpdate;
import com.fhoner.exifrename.util.FilenamePattern;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
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
@Log4j
public class FileService extends Observable {

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
        log.info("scanning directory " + directory);
        File source = new File(directory);
        if (!source.exists()) {
            throw new FileNotFoundException();
        }

        File[] files = source.listFiles((dir, name) -> {
            String extension = FilenameUtils.getExtension(name).toLowerCase();
            if (FILE_EXTENSION_WHITELIST.contains(extension)) {
                log.debug("file added: " + dir + "/" + name);
                return true;
            } else {
                log.debug("file not supported: " + dir + "/" + name);
                return false;
            }
        });
        log.info(files.length + " files added");
        this.files.addAll(Arrays.asList(files));
    }

    /**
     * Renames all found files previously by copying them into destination folder.
     *
     * @param pattern     The pattern for ne new file names.
     * @param destination Destination directory where files will be stored.
     * @throws Exception Thrown on several errors (tbd).
     */
    public void createFiles(@NonNull FilenamePattern pattern, @NonNull String destination) throws Exception {
        if (destination.charAt(destination.length() - 1) != '/') {
            destination = destination + "/";
        }

        log.info("starting creating files in " + destination);
        for (File file : files) {
            Metadata exif = ImageMetadataReader.readMetadata(file);
            Path source = Paths.get(file.getAbsolutePath());
            if (!Files.exists(Paths.get(destination))) {
                log.debug("destination directory does not exist. creating directory " + destination);
                (new File(destination)).mkdirs();
            }
            Path destinationPath = getNewFileName(pattern, destination, exif, file);
            log.info("moving " + source + " to " + destinationPath);
            Files.copy(source, destinationPath);
            sendUpdate(new FileServiceUpdate(files.size(), files.indexOf(file) + 1));
        }
        log.info("done. created " + files.size() + " files");
    }

    private void sendUpdate(FileServiceUpdate update) {
        setChanged();
        notifyObservers(update);
    }

    private Path getNewFileName(FilenamePattern pattern, String destination, Metadata exif, File source) throws Exception {
        Path result;
        String extension = "." + FilenameUtils.getExtension(source.getName());
        Integer number = null;
        boolean recreate = false;

        String formattedFilename = pattern.formatFilename(exif);
        do {
            String numberStr = number == null ? "" : " (" + number + ")";
            StringBuilder dest = new StringBuilder();
            dest.append(destination);
            dest.append(formattedFilename);
            dest.append(numberStr);
            dest.append(extension);

            result = Paths.get(dest.toString());
            if (Files.exists(result)) {
                number = number == null ? 1 : number + 1;
                recreate = true;
            } else {
                recreate = false;
            }
        } while (recreate);
        return result;
    }

}
