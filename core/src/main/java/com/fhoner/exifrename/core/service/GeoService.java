package com.fhoner.exifrename.core.service;

import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;

public interface GeoService {

    OSMRecord reverseLookup(GpsRecord lat, GpsRecord lon) throws GpsReverseLookupException;

}
