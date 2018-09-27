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
        return new ArrayList<IPTCDataSet>() {{
            add(new IPTCDataSet(IPTCApplicationTag.COPYRIGHT_NOTICE, copyrightNotice));
            add(new IPTCDataSet(IPTCApplicationTag.KEY_WORDS, keyWords));
            add(new IPTCDataSet(IPTCApplicationTag.CONTENT_LOCATION_NAME, locationName));
            add(new IPTCDataSet(IPTCApplicationTag.CONTENT_LOCATION_CODE, locationCode));
            add(new IPTCDataSet(IPTCApplicationTag.SUB_LOCATION, subLocation));
            add(new IPTCDataSet(IPTCApplicationTag.CITY, city));
            add(new IPTCDataSet(IPTCApplicationTag.PROVINCE_STATE, provinceState));
            add(new IPTCDataSet(IPTCApplicationTag.COUNTRY_CODE, countryCode));
            add(new IPTCDataSet(IPTCApplicationTag.COUNTRY_NAME, countryName));
        }};
    }

}
