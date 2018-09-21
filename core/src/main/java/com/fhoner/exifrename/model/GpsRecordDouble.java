package com.fhoner.exifrename.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class GpsRecordDouble {

    private GpsRecord lat;
    private GpsRecord lon;

}
