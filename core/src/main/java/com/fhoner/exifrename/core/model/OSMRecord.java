package com.fhoner.exifrename.core.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OSMRecord {

    @SerializedName("place_id")
    private long placeId;

    private String licence;

    @SerializedName("osm_type")
    private String osmType;

    @SerializedName("osm_id")
    private long osmId;

    private double lat;
    private double lon;

    @SerializedName("display_name")
    private String displayName;

    private Address address;
    private transient Object boundingbox;

}
