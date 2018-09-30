package com.fhoner.exifrename.core.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileServiceUpdate {

    public enum Reason {PROGRESS, ABORT}

    private Reason reason;
    private int filesCount;
    private int filesDone;

    public boolean isDone() {
        return filesCount == filesDone;
    }

}
