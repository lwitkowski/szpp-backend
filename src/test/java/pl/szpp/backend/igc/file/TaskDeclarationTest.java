package pl.szpp.backend.igc.file;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TaskDeclarationTest {

    static final String HEADER = "C280620101938280620000002";
    static final String TAKEOFF = "C0000000N00000000E";
    static final String START = "C5053100N01912283E310CZWARUDNI";
    static final String WP_1 = "C5054150N01750233E346POKOJ";
    static final String WP_2 = "C5050267N02000883E223WLOSZCZOW";
    static final String FINISH = "C5053100N01912283E310CZWARUDNI";
    static final String LANDING = "C0000000N00000000E";

    @Test
    void declarationShouldIdentifyStartFinishAndTurnPoints() {
        TaskDeclaration declaration = createTestDeclaration();

        assertThat(declaration.getStart().description).isEqualTo("310CZWARUDNI");
        assertThat(declaration.getFinish().description).isEqualTo("310CZWARUDNI");
        assertThat(declaration.getTurnPoints()).hasSize(2);
        assertThat(declaration.getTurnPoints().get(0).description).isEqualTo("346POKOJ");
        assertThat(declaration.getTurnPoints().get(1).description).isEqualTo("223WLOSZCZOW");
        assertThat(declaration.fromStartToFinish()).hasSize(4);
    }

    @Test
    void distanceCalculationShouldWorkd() {
        TaskDeclaration declaration = createTestDeclaration();

        assertThat(declaration.distance()).isEqualTo(305.9888582831112);
    }

    private TaskDeclaration createTestDeclaration() {
        return new TaskDeclaration(List.of(
            WayPoint.parseIgcLine(HEADER),
            WayPoint.parseIgcLine(TAKEOFF),
            WayPoint.parseIgcLine(START),
            WayPoint.parseIgcLine(WP_1),
            WayPoint.parseIgcLine(WP_2),
            WayPoint.parseIgcLine(FINISH),
            WayPoint.parseIgcLine(LANDING)
        ));
    }

}
