package com.fhoner.exifrename.core.service;

import lombok.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileService {

    void addFiles(@NonNull String directory) throws FileNotFoundException;

    void formatFiles(@NonNull String pattern, @NonNull String destination) throws IOException;

    void cancel();

}
