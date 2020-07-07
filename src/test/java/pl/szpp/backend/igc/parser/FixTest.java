package pl.szpp.backend.igc.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixTest {

    static final String RAW_FIX_1 = "B1935253428508N45925277EA123971235100000";
    static final String RAW_FIX_2 = "B1935253428508S05925277WA000970005100000";

    @Test
    void parseShouldCoverAllFields() {
        Fix fix = new Fix(RAW_FIX_1);

        assertThat(fix.getTime()).isEqualTo("19:35:25");
        assertThat(fix.latitude).isEqualTo(34.47513333333333);
        assertThat(fix.longitude).isEqualTo(459.42128333333335);
        assertThat(fix.getAltitudeBaro()).isEqualTo(12397);
        assertThat(fix.getAltitudeGps()).isEqualTo(12351);
    }

    @Test
    void parseShouldSupportSouthernAndWesternHemisphere() {
        Fix fix = new Fix(RAW_FIX_2);

        assertThat(fix.getTime()).isEqualTo("19:35:25");
        assertThat(fix.latitude).isEqualTo(-34.47513333333333);
        assertThat(fix.longitude).isEqualTo(-59.421283333333335);
        assertThat(fix.getAltitudeBaro()).isEqualTo(97);
        assertThat(fix.getAltitudeGps()).isEqualTo(51);
    }

    @Test
    void gpsAltitudeShouldHavePrecedenceOverBaro() {
        Fix fix = new Fix(RAW_FIX_2);

        assertThat(fix.getAltitudeGps()).isEqualTo(51);
        assertThat(fix.getAltitudeBaro()).isEqualTo(97);
        assertThat(fix.getAltitude()).isEqualTo(51);
    }

}
