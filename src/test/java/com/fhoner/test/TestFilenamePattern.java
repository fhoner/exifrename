package com.fhoner.test;

import com.drew.metadata.Metadata;
import com.fhoner.exifrename.util.FilenamePattern;
import org.junit.Test;

public class TestFilenamePattern {

    @Test
    public void withoutCity() throws Exception {
        FilenamePattern.fromString("no").formatFilename(new Metadata());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterFromString() {
        FilenamePattern.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnNullParameterOnFormat() throws Exception {
        FilenamePattern.fromString("").formatFilename(null);
    }

}
