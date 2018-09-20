package com.fhoner.exifrename.model;

import lombok.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
public class Address {

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
