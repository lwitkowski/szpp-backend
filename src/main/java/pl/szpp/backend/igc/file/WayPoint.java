package pl.szpp.backend.igc.file;

public class WayPoint extends LatLng {

    public static WayPoint parseIgcLine(String rawRecord) {
        return new WayPoint(
            parseLatitude(rawRecord.substring(1, 9)),
            parseLongitude(rawRecord.substring(9, 18)),
            rawRecord.substring(18)
        );
    }

    public final String description;

    public WayPoint(double lat, double lon, String description) {
        super(lat, lon);
        this.description = description;
    }

    public static boolean isCRecord(String line) {
        return line.startsWith("C");
    }

    public String getDescription() {
        return description;
    }

}
