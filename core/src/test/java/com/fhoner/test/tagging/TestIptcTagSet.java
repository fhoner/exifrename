package com.fhoner.test.tagging;

import com.fhoner.exifrename.core.tagging.IptcTagSet;
import com.icafe4j.image.meta.iptc.IPTCApplicationTag;
import com.icafe4j.image.meta.iptc.IPTCDataSet;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestIptcTagSet {

    @Test
    public void shouldCreateSet() {
        IptcTagSet.IptcTagSetBuilder setBuilder = IptcTagSet.builder()
                .city("Heilbronn");
        assertThat(setBuilder.toString(), notNullValue());

        IPTCDataSet city = setBuilder.build().collect().stream()
                .filter(tag -> tag.getTagEnum() == IPTCApplicationTag.CITY)
                .findAny()
                .orElse(null);
        assertThat(city, notNullValue());

        IPTCDataSet countryName = setBuilder.build().collect().stream()
                .filter(tag -> tag.getTagEnum() == IPTCApplicationTag.COUNTRY_NAME)
                .findAny()
                .orElse(null);
        assertThat(countryName, nullValue());
    }

}
