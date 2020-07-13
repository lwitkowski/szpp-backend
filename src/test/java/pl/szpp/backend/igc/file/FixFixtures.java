package pl.szpp.backend.igc.file;

public class FixFixtures {

    public static Fix fix(String lat, String lon, String time) {
        return Fix.parseIgcLine("B" + time.replaceAll(":", "") + lat + lon + "A123970009500000");
    }

}
