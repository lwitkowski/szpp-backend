package pl.szpp.backend.igc.file;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgcFileTest {

    @Test
    void builderShouldCalculateDerivedStatsProperly() {
        IgcFile igc = IgcFileFixtures.create();

        assertThat(igc.getTakeOffTime()).isEqualTo("19:36:11");
        assertThat(igc.getLandingTime()).isEqualTo("19:39:22");
        assertThat(igc.startAltitude).isEqualTo(51);
        assertThat(igc.minAltitude).isEqualTo(51);
        assertThat(igc.maxAltitude).isEqualTo(2351);
    }

}
