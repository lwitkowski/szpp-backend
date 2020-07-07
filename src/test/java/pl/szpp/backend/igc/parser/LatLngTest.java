package pl.szpp.backend.igc.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LatLngTest {

    @Test
    void parseLatituteNorthWestShouldWork() {
        assertThat(LatLng.parseLatitude("5206343N")).isEqualTo(52.105716666666666);
    }

    @Test
    void parseLatituteSouthShouldWork() {
        assertThat(LatLng.parseLatitude("5206343S")).isEqualTo(-52.105716666666666);
    }

    @Test
    void parseLongitudeWestShouldWork() {
        assertThat(LatLng.parseLongitude("00006198W")).isEqualTo(-0.1033);
    }

    @Test
    void parseLongitudeEastShouldWork() {
        assertThat(LatLng.parseLongitude("00006198E")).isEqualTo(0.1033);
    }

}
