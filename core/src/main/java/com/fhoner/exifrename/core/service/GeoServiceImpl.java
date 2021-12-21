package com.fhoner.exifrename.core.service;

import com.fhoner.exifrename.core.exception.GpsReverseLookupException;
import com.fhoner.exifrename.core.model.GpsRecord;
import com.fhoner.exifrename.core.model.OSMRecord;
import com.fhoner.exifrename.core.util.MetadataUtil;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;


/**
 * Does the communication between gps lookup server.
 */
@Log4j2
public class GeoServiceImpl implements GeoService {

    private static final long DURATION_PER_REQUEST = 1000;
    private final HashMap<String, OSMRecord> CACHE = new HashMap<>();

    private String apiUrl;

    private long lastExecution = -1;

    @Inject
    public GeoServiceImpl(@Named("geoserviceUrl") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Does a reverse lookup to retrieve address from gps location.
     *
     * @param lat Latitude.
     * @param lon Longtitude.
     * @return OSMRecord of the given position.
     * @throws GpsReverseLookupException Thrown when network error occurred.
     */
    public synchronized OSMRecord reverseLookup(GpsRecord lat, GpsRecord lon) throws GpsReverseLookupException {
        String key = lat.toString() + lon.toString();
        OSMRecord cached = CACHE.get(key);
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

        try {
            OSMRecord record = makeHttpRequest(lat, lon);
            if (record == null) {
                throw new RuntimeException();
            }
            CACHE.put(key, record);
            log.debug("got address " + record);
            lastExecution = System.currentTimeMillis();
            return record;
        } catch (Exception ex) {
            throw new GpsReverseLookupException("gps reverse lookup failed", ex);
        }
    }

    protected synchronized OSMRecord makeHttpRequest(GpsRecord lat, GpsRecord lon) throws UnirestException {
        var uri = getUrl(lat, lon);
        log.debug("sending request to " + uri);
        var result = Unirest.get(getUrl(lat, lon))
                .header("accept", "application/json")
                .header("request", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.106 Safari/537.36")
                .asObject(OSMRecord.class);
        if (result.getStatus() != 200) {
            throw new RuntimeException("Unexpected response code " + result.getStatus());
        }
        if (result.getParsingError().isPresent()) {
            throw result.getParsingError().get();
        }
        return result.getBody();
    }

    private String getUrl(GpsRecord lat, GpsRecord lon) {
        double dlat = MetadataUtil.convertGpsToDecimalDegree(lat);
        double dlon = MetadataUtil.convertGpsToDecimalDegree(lon);
        Map<String, String> data = new HashMap<>();
        data.put("latitude", String.valueOf(dlat));
        data.put("longtitude", String.valueOf(dlon));
        return StringSubstitutor.replace(this.apiUrl, data);
    }

    private void block() throws InterruptedException {
        long toReach = lastExecution + DURATION_PER_REQUEST;
        if (System.currentTimeMillis() < toReach) {
            log.info("blocking API due to time restrictions");
        }
        while (System.currentTimeMillis() < toReach) {
            Thread.sleep(100);
            log.trace("blocking api for 100ms...");
        }
    }

}
