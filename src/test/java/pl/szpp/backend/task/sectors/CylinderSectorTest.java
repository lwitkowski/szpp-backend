package pl.szpp.backend.task.sectors;

import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.LatLng;
import pl.szpp.backend.igc.file.WayPoint;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CylinderSectorTest {

    static final LatLng CENTER = new LatLng(0, 0);
    static final int RADIUS = 1000;

    final CylinderSector cylinderSector = new CylinderSector(
        new WayPoint(CENTER.latitude, CENTER.longitude, "EPRU"),
        RADIUS
    );

    @Test
    void findFirstFixInsideShouldDetectIntersectionWithFixInside() {
        Optional<SectorHit> hitOptional = cylinderSector.checkHit(
            fix("000000N", "0000100E", "10:10:00"),
            fix("000000N", "0000000W", "10:10:08")
        );

        assertThat(hitOptional).isNotEmpty();
        SectorHit hit = hitOptional.get();

        assertThat(hit.latitudeString()).isEqualTo("0000000N");
        assertThat(hit.longitudeString()).isEqualTo("00000000E");
        assertThat(hit.time).isEqualTo("10:10:08");
    }

    private Fix fix(String latitude, String longitude, String time) {
        return new Fix(latitude, longitude, LocalTime.parse(time), 0, 0);
    }

}
