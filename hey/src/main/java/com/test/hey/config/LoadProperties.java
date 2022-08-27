package com.test.hey.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class LoadProperties {

    private void init() throws IOException {
        Properties properties = new Properties();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.properties");
        properties.load(in);
        Set<String> strings = properties.stringPropertyNames();
        String param2 = properties.getProperty("param2");
    }
}
