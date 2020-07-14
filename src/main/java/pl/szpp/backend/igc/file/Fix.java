package pl.szpp.backend.igc.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class Fix extends LatLng {

    private static final int TIME_START_INDEX = 1;
    private static final int TIME_END_INDEX = 7;
    private static final int LAT_END_INDEX = 15;
    private static final int LON_END_INDEX = 24;
    private static final int FIX_VALIDITY_START_INDEX = 25;
    private static final int ALTITUDE_BARO_END_INDEX = 30;
    private static final int ALTITUDE_GPS_END_INDEX = 35;

    private static Logger logger = LoggerFactory.getLogger(Fix.class);

    public final LocalTime time;
    public final int altitudeBaro;
    public final int altitudeGps;

    public Fix(double latitude, double longitude, LocalTime time, int altBaro, int altGps) {
        super(latitude, longitude);
        this.time = time;
        this.altitudeBaro = altBaro;
        this.altitudeGps = altGps;
    }

    public Fix(String latitude, String longitude, LocalTime time, int altBaro, int altGps) {
        super(latitude, longitude);
        this.time = time;
        this.altitudeBaro = altBaro;
        this.altitudeGps = altGps;
    }

    public static boolean isBRecord(String line) {
        return line.startsWith("B");
    }

    public static Fix parseIgcLine(String rawRecord) {
        return new Fix(
            rawRecord.substring(TIME_END_INDEX, LAT_END_INDEX),
            rawRecord.substring(LAT_END_INDEX, LON_END_INDEX),
            parseTime(rawRecord.substring(TIME_START_INDEX, TIME_END_INDEX)),
            Integer.parseInt(rawRecord.substring(FIX_VALIDITY_START_INDEX, ALTITUDE_BARO_END_INDEX)),
            Integer.parseInt(rawRecord.substring(ALTITUDE_BARO_END_INDEX, ALTITUDE_GPS_END_INDEX))
        );
    }
    private static LocalTime parseTime(String igcTime) {
        try {
            return LocalTime.of(
                Integer.parseInt(igcTime.substring(0, 2)),
                Integer.parseInt(igcTime.substring(2, 4)),
                Integer.parseInt(igcTime.substring(4, 6))
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public int getAltitude() {
        if (altitudeGps == 0) {
            return altitudeBaro;
        }
        return altitudeGps;
    }

}
