package com.fhoner.exifrename.service;

import com.fhoner.exifrename.exception.GpsReverseLookupException;
import com.fhoner.exifrename.model.OSMRecord;
import com.fhoner.exifrename.model.GpsRecord;
import com.fhoner.exifrename.util.MetadataUtil;
import org.apache.commons.text.StrSubstitutor;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * Does the communication between gps lookup server.
 */
public class GeoService {

    private static final String API_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longtitude}&addressdetails=1";

    private Client client = ClientBuilder.newClient();

    /**
     * Does a reverse lookup to retrieve address from gps location.
     *
     * @param lat Latitude.
     * @param lon Longtitude.
     * @return OSMRecord of the given position.
     * @throws GpsReverseLookupException Thrown when network error occurred.
     */
    public OSMRecord reverseLookup(GpsRecord lat, GpsRecord lon) throws GpsReverseLookupException {
        client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(getUrl(lat, lon));
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        if (response.getStatusInfo().getFamily() == SUCCESSFUL) {
            return response.readEntity(OSMRecord.class);
        } else {
            throw new GpsReverseLookupException("gps reverse lookup failed");
        }
    }

    private String getUrl(GpsRecord lat, GpsRecord lon) {
        double dlat = MetadataUtil.convertGpsToDecimalDegree(lat);
        double dlon = MetadataUtil.convertGpsToDecimalDegree(lon);
        Map<String, String> data = new HashMap<>();
        data.put("latitude", String.valueOf(dlat));
        data.put("longtitude", String.valueOf(dlon));
        return StrSubstitutor.replace(API_URL, data);
    }

}
