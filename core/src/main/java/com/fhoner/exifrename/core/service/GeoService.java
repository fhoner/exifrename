package com.fhoner.exifrename.core.service;

import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.util.MetadataUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeoService {

    private static final String API_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longtitude}&addressdetails=1";
    private static final long DURATION_PER_REQUEST = 1000;

    private static GeoService instance;

    private long lastExecution = -1;
    private HashMap<String, OSMRecord> cache = new HashMap<>();
    private Client client = ClientBuilder.newClient();

    public static synchronized GeoService getInstance() {
        if (instance == null) {
            log.info("creating singleton instance of " + GeoService.class.getName());
            instance = new GeoService();
        }
        return instance;
    }

    /**
     * Does a reverse lookup to retrieve address from gps location.
     *
     * @param lat Latitude.
     * @param lon Longtitude.
     * @return OSMRecord of the given position.
     * @throws GpsReverseLookupException Thrown when network error occurred.
     * @throws InterruptedException      Thrown when the blocking fails.
     */
    public synchronized OSMRecord reverseLookup(GpsRecord lat, GpsRecord lon) throws GpsReverseLookupException {
        String key = lat.toString() + lon.toString();
        OSMRecord cached = cache.get(key);
        if (cached != null) {
            log.debug("loaded address from cache");
            return cached;
        }

        try {
            block();
        } catch (InterruptedException ex) {
            log.error("delay between api calls failed", ex);
            throw new GpsReverseLookupException("delay between api calls caused an error", ex);
        }

        Response response = makeHttpRequest(lat, lon);
        if (response.getStatusInfo().getFamily() == SUCCESSFUL) {
            OSMRecord result = response.readEntity(OSMRecord.class);
            cache.put(key, result);
            log.debug("got address " + result);
            lastExecution = System.currentTimeMillis();
            return result;
        } else {
            throw new GpsReverseLookupException("gps reverse lookup failed");
        }
    }

    public void clearCache() {
        this.cache.clear();
    }

    protected synchronized Response makeHttpRequest(GpsRecord lat, GpsRecord lon) {
        WebTarget webTarget = client.target(getUrl(lat, lon));
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        log.debug("sending request to " + webTarget.getUri());
        return invocationBuilder.get();
    }

    private String getUrl(GpsRecord lat, GpsRecord lon) {
        double dlat = MetadataUtil.convertGpsToDecimalDegree(lat);
        double dlon = MetadataUtil.convertGpsToDecimalDegree(lon);
        Map<String, String> data = new HashMap<>();
        data.put("latitude", String.valueOf(dlat));
        data.put("longtitude", String.valueOf(dlon));
        return StrSubstitutor.replace(API_URL, data);
    }

    private void block() throws InterruptedException {
        long toReach = lastExecution + DURATION_PER_REQUEST;
        if (System.currentTimeMillis() < toReach) {
            log.info("blocking API due to time restrictions");
        }
        while (System.currentTimeMillis() < toReach) {
            Thread.sleep(200);
            log.trace("blocking api for 200ms...");
        }
    }

}
