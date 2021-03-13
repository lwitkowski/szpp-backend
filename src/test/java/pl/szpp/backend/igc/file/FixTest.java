package pl.szpp.backend.igc.file;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class FixTest {

    static final String RAW_FIX_1 = "B1935253428508N45925277EA123971235100000";

    @Test
    void parseIgcLineShouldParseAllFields() {
        Fix fix = Fix.parseIgcLine(RAW_FIX_1);

        assertThat(fix.time).isEqualTo("19:35:25");
        assertThat(fix.latitude).isEqualTo(34.47513333333333);
        assertThat(fix.longitude).isEqualTo(459.42128333333335);
        assertThat(fix.altitudeBaro).isEqualTo(12397);
        assertThat(fix.altitudeGps).isEqualTo(12351);
    }

    @Test
    void gpsAltitudeShouldHavePrecedenceOverBaro() {
        Fix fix = new Fix("000000N", "0000000E", LocalTime.now(), 12, 34);

        assertThat(fix.getAltitude()).isEqualTo(34);
    }

}
