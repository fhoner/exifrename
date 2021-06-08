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
public class OSMRecord {

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

    private Address address;
    private transient Object boundingbox;

}
