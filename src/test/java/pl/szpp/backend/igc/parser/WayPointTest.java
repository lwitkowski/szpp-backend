package pl.szpp.backend.igc.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WayPointTest {

    @Test
    void parseShouldCoverAllFields() {
        WayPoint wayPoint = new WayPoint("C5111359N00101899WTAKEOFFLasham Clubhouse");

        assertThat(wayPoint.latitude).isEqualTo(51.18931666666667);
        assertThat(wayPoint.longitude).isEqualTo(-1.03165);
        assertThat(wayPoint.getDescription()).isEqualTo("TAKEOFFLasham Clubhouse");
    }

}
