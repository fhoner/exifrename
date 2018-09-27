package com.fhoner.test.service;

import com.fhoner.exifrename.core.util.FilenamePattern;
import org.junit.Test;

public class TestFilenamePattern {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterFromString() {
        FilenamePattern.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterOnFormat() throws Exception {
        FilenamePattern.fromString("").formatFilename(null);
    }

}
