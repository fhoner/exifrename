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

    private String water;
    private String footway;
    private String neighbourhood;
    private String village;
    private String town;
    private String city;
    private String city_district;
    private String county;
    private String state;
    private String country;
    private String postcode;

    @XmlElement(name = "country_code")
    private String countryCode;

    public String getVillage() {
        return getTownCityVillage();
    }

    public String getCity() {
        return getTownCityVillage();
    }

    public String getTown() {
        return getTownCityVillage();
    }

    private String getTownCityVillage() {
        if (village != null && !String.valueOf(village).isEmpty()) {
            return village;
        } else if (city != null && !String.valueOf(city).isEmpty()) {
            return city;
        } else if (town != null && !String.valueOf(town).isEmpty()) {
            return town;
        }
        return null;
    }

    public String getCountryCode() {
        return countryCode == null ? null : countryCode.toUpperCase();
    }

}
