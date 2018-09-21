package com.fhoner.exifrename.model;

import lombok.*;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a single gps record, which means two instances are needed, one for latitude and another one for
 * longtitude. Can only be created by using distance, minutes, seconds type.
 * Use {@link com.fhoner.exifrename.util.MetadataUtil#convertGpsToDecimalDegree(GpsRecord)} for converting.
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
@Log4j
public class GpsRecord {

    public enum Ref {N, S, E, W}

    private Ref ref;
    private int degrees;
    private double minutes;
    private double seconds;

    private GpsRecord() {
    }

    /**
     * Parses a string in given format: N 43° 39' 21.62".
     *
     * @param str String to parse.
     * @return An instance of {@link GpsRecord}.
     */
    public static GpsRecord parseString(@NonNull String str) {
        log.debug("parsing GpsRecord: " + str);
        List<String> split = new ArrayList<>();
        Arrays.asList(str.split(" "))
                .stream()
                .forEach(s -> split.add(escapeStr(s)));

        return GpsRecord.builder()
                .ref(Ref.valueOf(split.get(0)))
                .degrees(Integer.parseInt(split.get(1)))
                .minutes(Double.parseDouble(split.get(2)))
                .seconds(Double.parseDouble(split.get(3)))
                .build();
    }

    private static String escapeStr(String str) {
        return str
                .replace("°", "")
                .replace("'", "")
                .replace("\"", "");
    }

}
