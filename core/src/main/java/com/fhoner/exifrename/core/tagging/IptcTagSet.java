package com.fhoner.exifrename.core.tagging;

import com.icafe4j.image.meta.iptc.IPTCApplicationTag;
import com.icafe4j.image.meta.iptc.IPTCDataSet;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
public class IptcTagSet {

    private String copyrightNotice;
    private String keyWords;
    private String locationName;
    private String locationCode;
    private String subLocation;
    private String city;
    private String provinceState;
    private String countryCode;
    private String countryName;

    public List<IPTCDataSet> collect() {
        List<IPTCDataSet> list = new ArrayList<>();
        if (copyrightNotice != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.COPYRIGHT_NOTICE, copyrightNotice));
        }
        if (keyWords != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.KEY_WORDS, keyWords));
        }
        if (locationName != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.CONTENT_LOCATION_NAME, locationName));
        }
        if (locationCode != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.CONTENT_LOCATION_CODE, locationCode));
        }
        if (subLocation != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.SUB_LOCATION, subLocation));
        }
        if (city != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.CITY, city));
        }
        if (provinceState != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.PROVINCE_STATE, provinceState));
        }
        if (countryCode != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.COUNTRY_CODE, countryCode));
        }
        if (countryName != null) {
            list.add(new IPTCDataSet(IPTCApplicationTag.COUNTRY_NAME, countryName));
        }
        return list;
    }

}
