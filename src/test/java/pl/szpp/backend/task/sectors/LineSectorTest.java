package pl.szpp.backend.task.sectors;

import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.WayPoint;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szpp.backend.igc.file.FixFixtures.fix;

class LineSectorTest {

    static final WayPoint START = WayPoint.parseIgcLine("C5053100N01912283ESTART");
    static final int LINE_WIDTH_METERS = 10000;

    final LineSector lineSector = LineSector.forRoute(
        START,
        LINE_WIDTH_METERS,
        WayPoint.parseIgcLine("C5053100N01750233EWP1")
    );

    @Test
    void checkIntersectionForVerticalStartLine() {
        List<Fix> track = List.of(
            fix("5053100N", "01912480E", "19:35:00"),
            fix("5053100N", "01912180E", "19:35:08")
        );

        Optional<SectorHit> hitOptional = lineSector.findFirstHit(track);

        assertThat(hitOptional).isNotEmpty();
        SectorHit hit = hitOptional.get();
        assertThat(hit.latitudeString()).isEqualTo("505310N");
        assertThat(hit.longitudeString()).isEqualTo("0191228E");
        assertThat(hit.time).isEqualTo("19:35:06");
    }

    @Test
    void checkNoIntersectionForVerticalStartLine() {
        List<Fix> track = List.of(
            fix("5053100N", "01912380E", "19:35:00"),
            fix("5053100N", "01912290E", "19:35:10")
        );

        Optional<SectorHit> hitOptional = lineSector.findFirstHit(track);

        assertThat(hitOptional).isEmpty();
    }

    @Test
    void checkNoIntersectionForParallel() {
        List<Fix> track = List.of(
            fix("5055100N", "01912380E", "19:35:00"),
            fix("5055100N", "01912290E", "19:35:10")
        );

        Optional<SectorHit> hitOptional = lineSector.findFirstHit(track);

        assertThat(hitOptional).isEmpty();
    }

}
