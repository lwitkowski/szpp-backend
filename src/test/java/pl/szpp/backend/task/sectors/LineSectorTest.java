package pl.szpp.backend.task.sectors;

import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.WayPoint;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szpp.backend.igc.file.FixFixtures.fix;

class LineSectorTest {

    static final WayPoint START = new WayPoint("5053100N", "01912283E", "START");
    static final int LINE_WIDTH_METERS = 10000;

    final LineSector lineSector = LineSector.forRoute(
        START,
        LINE_WIDTH_METERS,
        new WayPoint("5053100N", "01750233E", "WP1")
    );

    @Test
    void checkIntersectionForVerticalStartLine() {
        Optional<SectorHit> hitOptional = lineSector.checkHit(
            fix("5053100N", "01912480E", "19:35:00"),
            fix("5053100N", "01912180E", "19:35:08")
        );

        assertThat(hitOptional).isNotEmpty();
        SectorHit hit = hitOptional.get();
        assertThat(hit.latitudeString()).isEqualTo("5053100N");
        assertThat(hit.longitudeString()).isEqualTo("01912283E");
        assertThat(hit.time).isEqualTo("19:35:06");
    }

    @Test
    void noHitForPerpendicularButSeparateSegment() {
        Optional<SectorHit> hitOptional = lineSector.checkHit(
            fix("5253100N", "01912283E", "19:35:00"),
            fix("5153100N", "01912283E", "19:35:10")
        );

        assertThat(hitOptional).isEmpty();
    }

    @Test
    void noHitForParallelSegment() {
        Optional<SectorHit> hitOptional = lineSector.checkHit(
            fix("5055100N", "01912380E", "19:35:00"),
            fix("5055100N", "01912290E", "19:35:10")
        );

        assertThat(hitOptional).isEmpty();
    }

}
