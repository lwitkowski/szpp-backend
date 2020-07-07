package pl.szpp.backend.igc.parser;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class IgcParserTest {

    IgcParser parser = new IgcParser();

    @Test
    void parserShouldWork() throws Exception {
        FileInputStream is = new FileInputStream(new File("src/test/resources/igc/953LNF91.IGC"));

        IgcFile igc = parser.parse(is);

        assertThat(igc.getDate()).isEqualTo("2019-05-03");
        assertThat(igc.getTaskDeclaration().wpCount()).isEqualTo(6);
        assertThat(igc.getTrack()).hasSize(1308);
        assertThat(igc.getMaxAltitude()).isEqualTo(2292);
        assertThat(igc.getMinAltitude()).isEqualTo(435);

        assertThat(igc.getTakeOffTime()).isEqualTo("11:50:48");
        assertThat(igc.getTakeOffAltitude()).isEqualTo(443);

        assertThat(igc.getLandingTime()).isEqualTo("13:37:04");
        assertThat(igc.getLandingAltitude()).isEqualTo(531);

        assertThat(igc.getFlightTime().toMinutes()).isEqualTo(106);
    }

}
