package pl.szpp.backend.igc.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class IgcFileFixtures {

    public static final File IGC_FILE = new File("src/test/resources/igc/953LNF91.IGC");

    public static final String RAW_RECORD_1 = "B1935253428508S05925277WA000970005100000";
    public static final String RAW_RECORD_2 = "B1936113428508N45925277EA123970009500000";
    public static final String RAW_RECORD_3 = "B1939223428508N45925277EA123970235100000";

    public static final String HEADER = "C280620101938280620000002";
    public static final String TAKEOFF = "C0000000N00000000E";
    public static final String WP_START = "C5053100N01912283E310CZWARUDNI";
    public static final String WP_1 = "C5054150N01750233E346POKOJ";
    public static final String WP_2 = "C5050267N02000883E223WLOSZCZOW";
    public static final String WP_FINISH = "C5053100N01912283E310CZWARUDNI";
    public static final String LANDING = "C0000000N00000000E";

    public static IgcFile create() {
        IgcFile.Builder igc = IgcFile.builder()
            .withDate("190703");

        igc.appendWayPoint(WayPoint.parseIgcLine(HEADER));
        igc.appendWayPoint(WayPoint.parseIgcLine(TAKEOFF));
        igc.appendWayPoint(WayPoint.parseIgcLine(WP_START));
        igc.appendWayPoint(WayPoint.parseIgcLine(WP_1));
        igc.appendWayPoint(WayPoint.parseIgcLine(WP_2));
        igc.appendWayPoint(WayPoint.parseIgcLine(WP_FINISH));
        igc.appendWayPoint(WayPoint.parseIgcLine(LANDING));

        igc.appendTrackPoint(Fix.parseIgcLine(RAW_RECORD_1));
        igc.appendTrackPoint(Fix.parseIgcLine(RAW_RECORD_2));
        igc.appendTrackPoint(Fix.parseIgcLine(RAW_RECORD_3));

        return igc.build();
    }

    public static IgcFile load(String filename) {
        IgcParser parser = new IgcParser();
        FileInputStream is;
        try {
            is = new FileInputStream(new File("src/test/resources/igc/" + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parser.parse(is);
    }

}
