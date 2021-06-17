package com.fhoner.exifrename.core.service;

import com.drew.metadata.Tag;
import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.exception.TagEmptyException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.util.MetadataUtil;
import com.google.inject.Inject;

import java.util.Map;

public class AddressServiceImpl implements AddressService {

    private GeoService geoService;

    @Inject
    public AddressServiceImpl(GeoService geoService) {
        this.geoService = geoService;
    }

    public OSMRecord getAddress(Map<String, Tag> tags) throws TagEmptyException, GpsReverseLookupException {
        GpsRecord lat = MetadataUtil.getLatitude(tags);
        GpsRecord lon = MetadataUtil.getLongtitude(tags);
        return this.geoService.reverseLookup(lat, lon);
    }

}
