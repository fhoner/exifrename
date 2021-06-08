package com.fhoner.exifrename.core.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String water;
    private String footway;
    private String neighbourhood;
    private String village;
    private String town;
    private String city;

    @SerializedName("city_district")
    private String cityDistrict;
    private String county;
    private String state;
    private String country;
    private String postcode;

    @SerializedName("country_code")
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
        if (village != null && !village.isEmpty()) {
            return village;
        } else if (city != null && !city.isEmpty()) {
            return city;
        } else if (town != null && !town.isEmpty()) {
            return town;
        }
        return null;
    }

    public String getCountryCode() {
        return countryCode == null ? null : countryCode.toUpperCase();
    }

}
