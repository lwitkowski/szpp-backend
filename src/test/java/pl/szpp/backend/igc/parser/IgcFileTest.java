package pl.szpp.backend.igc.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgcFileTest {

    static final String RAW_RECORD_1 = "B1935253428508S05925277WA000970005100000";
    static final String RAW_RECORD_2 = "B1936113428508N45925277EA123970009500000";
    static final String RAW_RECORD_3 = "B1939223428508N45925277EA123970235100000";

    @Test
    void finishShouldCalculateSomething() {
        IgcFile igc = new IgcFile();
        igc.appendTrackPoint(new Fix(RAW_RECORD_1));
        igc.appendTrackPoint(new Fix(RAW_RECORD_2));
        igc.appendTrackPoint(new Fix(RAW_RECORD_3));

        igc.finish();

        assertThat(igc.getTakeOffTime()).isEqualTo("19:36:11");
        assertThat(igc.getLandingTime()).isEqualTo("19:39:22");
        assertThat(igc.getStartAltitude()).isEqualTo(51);
        assertThat(igc.getMinAltitude()).isEqualTo(51);
        assertThat(igc.getMaxAltitude()).isEqualTo(2351);
    }

}
