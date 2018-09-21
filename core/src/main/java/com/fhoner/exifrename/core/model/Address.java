package com.fhoner.exifrename.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    private String water = "";
    private String footway = "";
    private String neighbourhood = "";
    private String village = "";
    private String county = "";
    private String state = "";
    private String country = "";
    private String postcode = "";

    @XmlElement(name = "country_code")
    private String countryCode = "";

}
