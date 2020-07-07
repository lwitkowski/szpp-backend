package pl.szpp.backend.igc.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskDeclarationTest {

    public static final String HEADER = "C280620101938280620000002";
    public static final String TAKEOFF = "C0000000N00000000E";
    public static final String START = "C5053100N01912283E310CZWARUDNI";
    public static final String WP_1 = "C5054150N01750233E346POKOJ";
    public static final String WP_2 = "C5050267N02000883E223WLOSZCZOW";
    public static final String FINISH = "C5053100N01912283E310CZWARUDNI";
    public static final String LANDING = "C0000000N00000000E";

    @Test
    void declarationShouldIdentifyStartFinishAndTurnPoints() {
        TaskDeclaration declaration = createTestDeclaration();

        assertThat(declaration.getStart().description).isEqualTo("310CZWARUDNI");
        assertThat(declaration.getFinish().description).isEqualTo("310CZWARUDNI");
        assertThat(declaration.getTurnPoints()).hasSize(2);
        assertThat(declaration.getTurnPoints().get(0).description).isEqualTo("346POKOJ");
        assertThat(declaration.getTurnPoints().get(1).description).isEqualTo("223WLOSZCZOW");
    }

    private TaskDeclaration createTestDeclaration() {
        TaskDeclaration declaration = new TaskDeclaration();
        declaration.appendWayPoint(new WayPoint(HEADER));
        declaration.appendWayPoint(new WayPoint(TAKEOFF));
        declaration.appendWayPoint(new WayPoint(START));
        declaration.appendWayPoint(new WayPoint(WP_1));
        declaration.appendWayPoint(new WayPoint(WP_2));
        declaration.appendWayPoint(new WayPoint(FINISH));
        declaration.appendWayPoint(new WayPoint(LANDING));
        return declaration;
    }

}
