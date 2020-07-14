package pl.szpp.backend.task.sectors;

import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.LatLng;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.szpp.backend.igc.file.FixFixtures.fix;

class SectorHitTest {

    @Test
    void interpolateHitTimeInTheMiddleShouldCeilSeconds() {
        LocalTime intersectionTime = SectorHit.interpolateHitTime(
            fix("5053100N", "01912480E", "19:35:00"),
            fix("5053100N", "01912680E", "19:35:20"),
            new LatLng("5053100N", "01912580E")
        );

        assertThat(intersectionTime).isEqualTo("19:35:11");
    }

}
