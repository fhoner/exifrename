package com.fhoner.exifrename.core.service;

import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.util.MetadataUtil;
import lombok.extern.log4j.Log4j;
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
@Log4j
public class GeoService {

    private static final String API_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longtitude}&addressdetails=1";

    private HashMap<String, OSMRecord> cache = new HashMap<>();
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
        String key = lat.toString() + lon.toString();
        OSMRecord cached = cache.get(key);
        if (cached != null) {
            log.debug("loaded address from cache");
            return cached;
        }

        client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(getUrl(lat, lon));
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        log.debug("sending request to " + webTarget.getUri());
        Response response = invocationBuilder.get();

        if (response.getStatusInfo().getFamily() == SUCCESSFUL) {
            OSMRecord result = response.readEntity(OSMRecord.class);
            cache.put(key, result);
            log.debug("got address " + result);
            return result;
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
