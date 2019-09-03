package com.fhoner.test.service;

import com.fhoner.exifrename.core.service.FileService;
import com.fhoner.exifrename.core.util.FilenamePattern;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class TestFileService {

    private static final String SAMPLE_IMAGE_NAME = "images/sample.JPG";
    private static final String SAMPLE_IMAGE_NAME_NO_GPS = "images/sample_nogps.jpg";
    private static final String SAMPLE_IMAGE_DUPLICATED_KEYS = "images/bug_190903.JPG";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString();
    private static final String DESTINATION_DIR = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString();
    private static final String FILENAME = TEMP_DIR + "/" + UUID.randomUUID().toString().substring(0, 7) + ".jpg";

    private FileService fileService;

    @Before
    public void init() throws Exception {
        this.fileService = new FileService();
    }

    @After
    public void cleanup() throws IOException {
        FileUtils.deleteDirectory(new File(DESTINATION_DIR));
    }

    private void copyFile(String filename) throws IOException {
        Path dest = Paths.get(FILENAME);
        String destPath = dest.getParent().toString();
        (new File(destPath)).mkdirs();

        File file = new File(getClass().getClassLoader().getResource(filename).getFile());
        Path source = Paths.get(file.getAbsolutePath());
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void shouldRenameFileWithGps() throws Exception {
        copyFile(SAMPLE_IMAGE_NAME);
        FilenamePattern pattern = FilenamePattern.fromString("%c test");
        fileService.addFiles(TEMP_DIR);
        fileService.formatFiles(pattern, DESTINATION_DIR);
        File newFile = new File(DESTINATION_DIR + "/Dax test.jpg");
        assertThat(newFile, anExistingFile());
    }

    @Test
    public void shouldRenameFileWithoutGps() throws Exception {
        copyFile(SAMPLE_IMAGE_NAME_NO_GPS);
        FilenamePattern pattern = FilenamePattern.fromString("%c test");
        fileService.addFiles(TEMP_DIR);
        fileService.formatFiles(pattern, DESTINATION_DIR);
        File newFile = new File(DESTINATION_DIR + "/test [No-GPS].jpg");
        assertThat(newFile, anExistingFile());
    }

    @Test
    public void shouldRenameWithDuplicatedTagKeys() throws Exception {
        copyFile(SAMPLE_IMAGE_DUPLICATED_KEYS);
        FilenamePattern pattern = FilenamePattern.fromString("%c test");
        fileService.addFiles(TEMP_DIR);
        fileService.formatFiles(pattern, DESTINATION_DIR);
        File newFile = new File(DESTINATION_DIR + "/test [No-GPS].jpg");
        assertThat(newFile, anExistingFile());
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldThrowWhenFileNotExists() throws Exception {
        fileService.addFiles("/wont/exist/ever/i/guess");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterAddFiles() throws Exception {
        fileService.addFiles(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterRename() throws Exception {
        fileService.formatFiles(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterRename2() throws Exception {
        fileService.formatFiles(FilenamePattern.fromString("%c"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterRename3() throws Exception {
        fileService.formatFiles(null, "");
    }

}
