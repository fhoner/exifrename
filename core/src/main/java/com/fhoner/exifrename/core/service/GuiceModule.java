package com.fhoner.exifrename.core.service;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.IOException;
import java.util.Properties;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AddressService.class).to(AddressServiceImpl.class);
        bind(FileFormatter.class).to(FileFormatterImpl.class);
        bind(FileService.class).to(FileServiceImpl.class);
        bind(GeoService.class).to(GeoServiceImpl.class);
        Names.bindProperties(binder(), getProperties());
    }

    private Properties getProperties() {
        try {
            Properties properties = new Properties();
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("application.properties"));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
