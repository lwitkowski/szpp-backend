package pl.szpp.backend.igc.parser;

class WayPoint extends LatLng {

    public final String raw;
    public final String description;

    public WayPoint(String rawRecord) {
        super(
            LatLng.parseLatitude(rawRecord.substring(1, 9)),
            LatLng.parseLongitude(rawRecord.substring(9, 18))
        );
        raw = rawRecord;
        description = rawRecord.substring(18);
    }

    public static boolean isCRecord(String line) {
        return line.startsWith("C");
    }

    public String getDescription() {
        return description;
    }

}
