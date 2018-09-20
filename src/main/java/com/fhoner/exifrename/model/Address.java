package com.fhoner.exifrename.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Getter
@Setter
@ToString
public class Address {

    @Getter
    @Setter
    @ToString
    public class Addr {
        private String water;
        private String footway;
        private String neighbourhood;
        private String village;
        private String county;
        private String state;
        private String country;
        private String postcode;

        @XmlElement(name = "country_code")
        private String countryCode;
    }

    @XmlElement(name = "place_id")
    private long placeId;

    private String licence;

    @XmlElement(name = "osm_type")
    private String osmType;

    @XmlElement(name = "osm_id")
    private long osmId;

    private double lat;
    private double lon;

    @XmlElement(name = "display_name")
    private String displayName;

    private Addr address;
    private transient Object boundingbox;

}
