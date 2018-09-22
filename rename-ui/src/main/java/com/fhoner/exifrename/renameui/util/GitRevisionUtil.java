package com.fhoner.exifrename.renameui.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GitRevisionUtil {

    public enum Key {
        HASH("git.commit.id.abbrev");

        private final String text;

        Key(String val) {
            this.text = val;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static final String GIT_INFO_FILENAME = "git.properties";
    private static final InputStream FILE = GitRevisionUtil.class.getClassLoader().getResourceAsStream(GIT_INFO_FILENAME);

    private Properties properties;

    public GitRevisionUtil() throws IOException {
        properties = new Properties();
        properties.load(FILE);
    }

    public String get(Key key) {
        return properties.getProperty(key.toString());
    }

}
