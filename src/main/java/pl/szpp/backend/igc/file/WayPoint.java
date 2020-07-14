package pl.szpp.backend.igc.file;

import java.util.concurrent.atomic.AtomicInteger;

public class WayPoint extends LatLng {

    private static final AtomicInteger nextUid = new AtomicInteger(0);

    private final int id = nextUid.incrementAndGet();
    public final String description;

    public WayPoint(double latitude, double longitude, String description) {
        super(latitude, longitude);
        this.description = description;
    }

    public WayPoint(String latitude, String longitude, String description) {
        super(latitude, longitude);
        this.description = description;
    }

    public static boolean isCRecord(String line) {
        return line.startsWith("C");
    }

    public static WayPoint parseIgcLine(String rawRecord) {
        return new WayPoint(
            rawRecord.substring(1, 9),
            rawRecord.substring(9, 18),
            rawRecord.substring(18)
        );
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "WP{#" + id + " " + description + ", " + latitudeString() + " " + longitudeString() + "}";
    }

}
