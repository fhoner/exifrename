package com.fhoner.test;

import com.fhoner.exifrename.service.FileService;
import com.fhoner.exifrename.util.FilenamePattern;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;

@Log4j
public class TestSample {

    @Test
    @Ignore
    public void sample() {
        String source = "/home/fhoner/Desktop/source";
        String dest = "/home/fhoner/Desktop/dest";
        FilenamePattern pattern = FilenamePattern.fromString("%y-%m-%d - Motorradtour - %S, %c");

        try {
            FileService service = new FileService();
            service.addFiles(source);
            service.createFiles(pattern, dest);
        } catch (FileNotFoundException ex) {
            log.error("directory not found", ex);
        } catch (Exception ex) {
            System.out.println(ExceptionUtils.getStackTrace(ex));
        }
    }

}
