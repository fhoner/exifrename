package com.fhoner.exifrename.core.service;

import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.exception.TagEmptyException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.util.MetadataUtil;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class AddressService {

    private Map<String, Tag> tags;

    public OSMRecord getAddress() throws TagEmptyException, GpsReverseLookupException {
        GpsRecord lat = MetadataUtil.getLatitude(tags);
        GpsRecord lon = MetadataUtil.getLongtitude(tags);
        return new GeoService().reverseLookup(lat, lon);
    }

}
