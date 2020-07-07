package pl.szpp.backend.igc.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

class Fix extends LatLng {

    private static Logger logger = LoggerFactory.getLogger(Fix.class);

    private static final int TIME_START_INDEX = 1;
    private static final int TIME_END_INDEX = 7;
    private static final int LAT_END_INDEX = 15;
    private static final int LON_END_INDEX = 24;
    private static final int FIX_VALIDITY_START_INDEX = 25;
    private static final int ALTITUDE_BARO_END_INDEX = 30;
    private static final int ALTITUDE_GPS_END_INDEX = 35;

    public final String raw;
    public final LocalTime time;
    public final int altitudeBaro;
    public final int altitudeGps;

    public Fix(String rawRecord) {
        super(
            LatLng.parseLatitude(rawRecord.substring(TIME_END_INDEX, LAT_END_INDEX)),
            LatLng.parseLongitude(rawRecord.substring(LAT_END_INDEX, LON_END_INDEX))
        );
        raw = rawRecord;
        time = parseTime(rawRecord.substring(TIME_START_INDEX, TIME_END_INDEX));
        altitudeBaro = Integer.parseInt(rawRecord.substring(FIX_VALIDITY_START_INDEX, ALTITUDE_BARO_END_INDEX));
        altitudeGps = Integer.parseInt(rawRecord.substring(ALTITUDE_BARO_END_INDEX, ALTITUDE_GPS_END_INDEX));
    }

    public static boolean isBRecord(String line) {
        return line.startsWith("B");
    }

    private LocalTime parseTime(String igcTime) {
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

    public LocalTime getTime() {
        return time;
    }

    public int getAltitudeBaro() {
        return altitudeBaro;
    }

    public int getAltitudeGps() {
        return altitudeGps;
    }

    public int getAltitude() {
        if (getAltitudeGps() == 0) {
            return getAltitudeBaro();
        }
        return getAltitudeGps();
    }

}
