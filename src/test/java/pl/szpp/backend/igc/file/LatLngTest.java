package pl.szpp.backend.igc.file;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LatLngTest {

    static final LatLng EPRU = new LatLng("5053100N", "01912089E");
    static final LatLng EPKA = new LatLng("5053487N", "02043529E");

    @Test
    void parseShouldSupportNorthernAndEasternHemispheres() {
        assertThat(EPRU.latitude).isEqualTo(50.88611111111111);
        assertThat(EPRU.longitude).isEqualTo(19.202472222222223);

        assertThat(EPKA.latitude).isEqualTo(50.89686111111111);
        assertThat(EPKA.longitude).isEqualTo(20.73136111111111);
    }

    @Test
    void parseShouldSupportSouthernAndWesternHemispheres() {
        LatLng coords = new LatLng("3428508S", "05925277W");

        assertThat(coords.latitude).isEqualTo(-34.48077777777778);
        assertThat(coords.longitude).isEqualTo(-59.42436111111111);
    }

    @Test
    void parseLongitudeShouldHandleVariableSecondsFieldLength() {
        assertThat(LatLng.parseLongitude("0960118")).isEqualTo(96.02166666666666);
        assertThat(LatLng.parseLongitude("09601180")).isEqualTo(96.02166666666666);
        assertThat(LatLng.parseLongitude("09601181")).isEqualTo(96.02169444444445);
    }

    @Test
    void distanceToForCloseCoordinatesShouldWork() {
        double distance = EPRU.distanceTo(EPKA);

        assertThat(distance).isEqualTo(107242.1924614171);
    }

    @Test
    void distanceToForDistantCoordinatesShouldWork() {
        LatLng from = new LatLng("500359N", "0054253W");
        LatLng to = new LatLng("783838S", "0030412W");

        double distance = from.distanceTo(to);

        assertThat(distance).isEqualTo(1.4313029156140221E7);
    }

    @Test
    void destinationToSouthShouldWork() {
        LatLng from = new LatLng("505310N", "0191209W");
        double bearing = 180.0;
        double distance = 10000;

        LatLng destination = from.destination(bearing, distance);

        assertThat(destination.latitudeString()).isEqualTo("504746N");
        assertThat(destination.longitudeString()).isEqualTo("0191209W");
    }

    @Test
    void destinationToEastOnEquatorShouldWork() {
        LatLng from = new LatLng("000000N", "0191209E");
        double bearing = 90.0;
        double distance = 10000;

        LatLng destination = from.destination(bearing, distance);

        assertThat(destination.latitudeString()).isEqualTo("000000N");
        assertThat(destination.longitudeString()).isEqualTo("0191733E");
    }


    @Test
    void destinationNorthEastHemisphereShouldWork() {
        LatLng from = new LatLng("505310N", "0191209E");
        double bearing = LatLng.parseLongitude("0890118");
        double distance = 106000;

        LatLng destination = from.destination(bearing, distance);

        assertThat(destination.latitudeString()).isEqualTo("505333N");
        assertThat(destination.longitudeString()).isEqualTo("0204249E");
    }

    @Test
    void latitudeString() {
        assertThat(latitudeString(0.0)).isEqualTo("000000N");
        assertThat(latitudeString(90.0)).isEqualTo("900000N");
        assertThat(latitudeString(45.5)).isEqualTo("453000N");
        assertThat(latitudeString(-45.5)).isEqualTo("453000S");
        assertThat(latitudeString(-45.76)).isEqualTo("454536S");
        assertThat(latitudeString(-45.77)).isEqualTo("454612S");
    }

    private String latitudeString(double latitude) {
        return new LatLng(latitude, 0.0).latitudeString();
    }

    @Test
    void longitudeString() {
        assertThat(longitudeString(0.0)).isEqualTo("0000000E");
        assertThat(longitudeString(90.0)).isEqualTo("0900000E");
        assertThat(longitudeString(45.5)).isEqualTo("0453000E");
        assertThat(longitudeString(-45.5)).isEqualTo("0453000W");
        assertThat(longitudeString(-45.76)).isEqualTo("0454536W");
        assertThat(longitudeString(-45.77)).isEqualTo("0454612W");
    }

    private String longitudeString(double longitude) {
        return new LatLng(0.0, longitude).longitudeString();
    }

}
