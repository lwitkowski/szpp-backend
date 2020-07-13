package pl.szpp.backend.task;

import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.IgcFile;
import pl.szpp.backend.igc.file.WayPoint;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szpp.backend.igc.file.IgcFileFixtures.HEADER;

class BasicDeclaredTaskTest {

    BasicDeclaredTask task = new BasicDeclaredTask();

    @Test
    void straightLineTaskCompleted() {
        IgcFile igc = create();

        TaskResult result = task.calculate(igc);

        assertThat(result.completed).isTrue();
        assertThat(result.duration()).isEqualTo(Duration.parse("PT35S"));
        assertThat(result.averageSpeed()).isEqualTo(571.8596227434449);
    }

    private IgcFile create() {
        IgcFile.Builder igc = IgcFile.builder()
            .withDate("190701");
        createDeclaration(igc);
        createTrack(igc);
        return igc.build();
    }

    private void createDeclaration(IgcFile.Builder igc) {
        igc.appendWayPoint(WayPoint.parseIgcLine(HEADER));
        igc.appendWayPoint(wp(-1)); // takeoff
        igc.appendWayPoint(wp(0)); // start
        igc.appendWayPoint(wp(1)); // tp1
        igc.appendWayPoint(wp(2)); // tp2
        igc.appendWayPoint(wp(3)); // finish
        igc.appendWayPoint(wp(4)); // landing
    }

    private void createTrack(IgcFile.Builder igc) {
        igc.appendTrackPoint(fix(-1, 0));
        igc.appendTrackPoint(fix(-1.1, 3));
        igc.appendTrackPoint(fix(-1.3, 5));
        igc.appendTrackPoint(fix(-0.3, 7));
        igc.appendTrackPoint(fix(0.1, 11)); // first after start, start crossed at 10th second
        igc.appendTrackPoint(fix(0.7, 20));
        igc.appendTrackPoint(fix(1, 25)); // tp1, 25th second
        igc.appendTrackPoint(fix(1.3, 28));
        igc.appendTrackPoint(fix(2, 35)); // tp2, 35th second
        igc.appendTrackPoint(fix(2.99, 44));
        igc.appendTrackPoint(fix(3.1, 45)); // first after finish line, 45th second
        igc.appendTrackPoint(fix(3.5, 47));
    }

    private WayPoint wp(double lonMinutes) {
        return new WayPoint(0, lonMinutes / 60, "");
    }

    private Fix fix(double lonMinutes, int secondsOffset) {
        return new Fix(0, lonMinutes / 60, LocalTime.ofSecondOfDay(secondsOffset), 100, 100);
    }

}
