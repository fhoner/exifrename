package com.fhoner.exifrename.core.service;

import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.exception.TagEmptyException;
import com.fhoner.exifrename.core.model.OSMRecord;

import java.util.Map;

public interface AddressService {

    OSMRecord getAddress(Map<String, Tag> tags) throws TagEmptyException, GpsReverseLookupException;

}
