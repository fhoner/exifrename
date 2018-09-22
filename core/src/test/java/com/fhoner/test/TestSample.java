package com.fhoner.test;

import com.drew.imaging.ImageProcessingException;
import com.fhoner.exifrename.core.service.FileService;
import com.fhoner.exifrename.core.util.FilenamePattern;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

@Log4j
public class TestSample {

    @Test
    public void sample() {
        String source = "/home/fhoner/Desktop/source";
        String dest = "/home/fhoner/Desktop/dest";
        FilenamePattern pattern = FilenamePattern.fromString("%y-%m-%d - Motorradtour - %S, %c");

        try {
            FileService service = new FileService();
            service.addFiles(source);
            service.createFiles(pattern, dest);
            service.getErrors().forEach((k, arr) -> {
                for (Exception ex : arr) {
                    System.out.print(k);
                    System.out.println("    > " + ex.getMessage());
                }
            });
        } catch (FileNotFoundException ex) {
            log.error("directory not found", ex);
        } catch (ImageProcessingException ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
        } catch (IOException ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
        }
    }

}
