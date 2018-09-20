package com.fhoner.test;

import com.fhoner.exifrename.service.FileService;
import com.fhoner.exifrename.util.FilenamePattern;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class TestFileService {

    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString().substring(0, 7);
    private static final String DESTINATION_DIR = "/home/fhoner/Desktop/" + UUID.randomUUID().toString().substring(0, 7);

    private final String filename = TEMP_DIR + "/" + UUID.randomUUID().toString().substring(0, 7) + ".jpg";

    private FileService fileService;

    @Before
    public void init() throws Exception {
        this.fileService = new FileService();
        File file = new File(getClass().getClassLoader().getResource(SAMPLE_IMAGE_NAME).getFile());
        Path source = Paths.get(file.getAbsolutePath());
        Path dest = Paths.get(filename);
        String destPath = dest.getParent().toString();
        (new File(destPath)).mkdirs();
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void shouldRenameFiles() throws Exception {
        FilenamePattern pattern = FilenamePattern.fromString("%c test");
        fileService.addFiles(TEMP_DIR);
        fileService.renameFiles(pattern, DESTINATION_DIR);

        File newFile = new File(DESTINATION_DIR + "/Capbreton test.jpg");
        assertThat(newFile, anExistingFile());
    }

}
