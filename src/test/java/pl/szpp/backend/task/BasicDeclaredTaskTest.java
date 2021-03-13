package pl.szpp.backend.task;

import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.IgcFile;
import pl.szpp.backend.igc.file.WayPoint;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szpp.backend.igc.file.IgcFileFixtures.HEADER;

class BasicDeclaredTaskTest {

    BasicDeclaredTask task = new BasicDeclaredTask();

    @Test
    void straightLineTaskCompleted() {
        IgcFile igc = forTrack(simpleTrack());

        TaskResult result = task.calculate(igc);

        assertThat(result.isCompleted()).isTrue();
        assertThat(result.duration).isEqualTo(Duration.parse("PT34S"));
        //assertThat(result.distanceTravelled()).isEqualTo(571.8596227434449);
        //assertThat(result.averageSpeed()).isEqualTo(571.8596227434449);
    }

    private IgcFile forTrack(List<Fix> track) {
        IgcFile.Builder igc = IgcFile.builder().withDate("190701");

        igc.appendWayPoint(WayPoint.parseIgcLine(HEADER));
        igc.appendWayPoint(wp(-1, "TAKEOFF"));
        igc.appendWayPoint(wp(0, "START"));
        igc.appendWayPoint(wp(3, "TP1"));
        igc.appendWayPoint(wp(-3, "TP2"));
        igc.appendWayPoint(wp(0, "FiNISH"));
        igc.appendWayPoint(wp(-1, "LANDING"));

        track.forEach(igc::appendTrackPoint);

        return igc.build();
    }

    private List<Fix> simpleTrack() {
        return List.of(
            fix(-0.3, 7),
            fix(0.1, 11), // after start line
            fix(2.7, 20),
            fix(3, 25), // tp1, 25th second
            fix(3.1, 28),
            fix(-2.7, 30),
            fix(-3.01, 35), // tp2, 35th second
            fix(-0.8, 44),
            fix(0.1, 45) // after finish line
        );
    }

    @Test
    void shouldUseLastStartAndFirstEndFix() {
        IgcFile igc = forTrack(createTrackWithMultipleStartAndFinishCrosses());

        TaskResult result = task.calculate(igc);

        assertThat(result.isCompleted()).isTrue();
        assertThat(result.duration).isEqualTo(Duration.parse("PT28S"));
        //assertThat(result.averageSpeed()).isEqualTo(690.17540675933);
    }

    private List<Fix> createTrackWithMultipleStartAndFinishCrosses() {
        return List.of(
            fix(-0.3, 7),
            fixAfterStartLine(),
            fix(-0.5, 13),
            fix(0.1, 16), // second start crossed at 15th second
            fix(2.7, 20),
            tp1(),
            fix(3.1, 28),
            fix(-2.7, 30),
            tp2(),
            fix(-0.8, 44),
            fix(0.1, 45), // after finish line
            fix(-2, 47),
            fix(-0.5, 49),
            fix(0.5, 51) // second finish line cross
        );
    }

    @Test
    void notCompletedTask() {
        IgcFile igc = forTrack(notCompletedTrack());

        TaskResult result = task.calculate(igc);

        assertThat(result.isCompleted()).isFalse();
        //assertThat(result.duration()).isEqualTo(Duration.parse("PT35S"));
        //assertThat(result.distanceTravelled()).isEqualTo(571.8596227434449);
        //assertThat(result.averageSpeed()).isEqualTo(571.8596227434449);
    }

    private List<Fix> notCompletedTrack() {
        return List.of(
            fix(-0.3, 7),
            fixAfterStartLine(),
            fix(2.7, 20),
            tp1(),
            fix(3.1, 28),
            fix(-2.7, 30),
            tp2(),
            photolanding()
        );
    }

    private Fix fixAfterStartLine() {
        return fix(0.1, 11);
    }

    private Fix tp1() {
        return fix(3, 25);
    }

    private Fix tp2() {
        return fix(-3.01, 35);
    }

    private Fix photolanding() {
        return fix(-2.8, 44);
    }

    private WayPoint wp(double lonMinutes, String description) {
        return new WayPoint(0, lonMinutes / 60, description);
    }

    private Fix fix(double lonMinutes, int secondsOffset) {
        return new Fix(0, lonMinutes / 60, LocalTime.ofSecondOfDay(secondsOffset), 100, 100);
    }

}
