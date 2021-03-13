package pl.szpp.backend.igc.file;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class IgcParserTest {

    IgcParser parser = new IgcParser();

    @Test
    void parserShouldWork() {
        InputStream is = ClassLoader.getSystemResourceAsStream("igc/953LNF91.IGC");

        IgcFile igc = parser.parse(is);

        assertThat(igc.date).isEqualTo("2019-05-03");
        assertThat(igc.declaration.countWaypoints()).isEqualTo(6);
        assertThat(igc.track).hasSize(1308);
        assertThat(igc.maxAltitude).isEqualTo(2292);
        assertThat(igc.minAltitude).isEqualTo(435);

        assertThat(igc.getTakeOffTime()).isEqualTo("11:50:48");
        assertThat(igc.getTakeOffAltitude()).isEqualTo(443);

        assertThat(igc.getLandingTime()).isEqualTo("13:37:04");
        assertThat(igc.getLandingAltitude()).isEqualTo(531);

        assertThat(igc.getFlightTime().toMinutes()).isEqualTo(106);
    }

    @Test
    void shouldSupportNewDateFormat() {
        InputStream is = ClassLoader.getSystemResourceAsStream("igc/HFDTEDATE.igc");

        IgcFile igc = parser.parse(is);

        assertThat(igc.date).isEqualTo("2020-06-28");
    }

}
