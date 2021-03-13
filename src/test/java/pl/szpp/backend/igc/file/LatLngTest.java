package pl.szpp.backend.igc.file;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LatLngTest {

    static final LatLng EPRU = new LatLng("5053100N", "01912089E");
    static final LatLng EPKA = new LatLng("5053487N", "02043529E");

    @Test
    void parseShouldSupportNorthernAndEasternHemispheres() {
        assertThat(EPRU.latitude).isEqualTo(50.885);
        assertThat(EPRU.longitude).isEqualTo(19.201483333333332);

        assertThat(EPKA.latitude).isEqualTo(50.89145);
        assertThat(EPKA.longitude).isEqualTo(20.725483333333333);
    }

    @Test
    void parseShouldSupportSouthernAndWesternHemispheres() {
        LatLng coords = new LatLng("3428508S", "05925277W");

        assertThat(coords.latitude).isEqualTo(-34.47513333333333);
        assertThat(coords.longitude).isEqualTo(-59.421283333333335);
    }

    @Test
    void parseLongitudeShouldHandleVariableSecondsFieldLength() {
        assertThat(LatLng.parseLongitude("0960118")).isEqualTo(96.01966666666667);
        assertThat(LatLng.parseLongitude("09601180")).isEqualTo(96.01966666666667);
        assertThat(LatLng.parseLongitude("09601181")).isEqualTo(96.01968333333333);
    }

    @Test
    void serializeAndDeserializeShouldBeReversible() {
        LatLng latLng = new LatLng("5141770N", "01612180E");

        assertThat(latLng.latitudeString()).isEqualTo("5141770N");
        assertThat(latLng.longitudeString()).isEqualTo("01612180E");
    }

    @Test
    void distanceToForCloseCoordinatesShouldWork() {
        double distance = EPRU.distanceTo(EPKA);

        assertThat(distance).isEqualTo(106902.52890696337);
    }

    @Test
    void distanceToForDistantCoordinatesShouldWork() {
        LatLng from = new LatLng("500359N", "0054253W");
        LatLng to = new LatLng("783838S", "0030412W");

        double distance = from.distanceTo(to);

        assertThat(distance).isEqualTo(1.4311827325162495E7);
    }

    @Test
    void destinationToSouthShouldWork() {
        LatLng from = new LatLng("505310N", "0191209W");
        double bearing = 180.0;
        double distance = 10000;

        LatLng destination = from.destination(bearing, distance);

        assertThat(destination.latitudeString()).isEqualTo("5047704N");
        assertThat(destination.longitudeString()).isEqualTo("01912090W");
    }

    @Test
    void destinationToEastOnEquatorShouldWork() {
        LatLng from = new LatLng("000000N", "0191209E");
        double bearing = 90.0;
        double distance = 10000;

        LatLng destination = from.destination(bearing, distance);

        assertThat(destination.latitudeString()).isEqualTo("0000000N");
        assertThat(destination.longitudeString()).isEqualTo("01917486E");
    }


    @Test
    void destinationNorthEastHemisphereShouldWork() {
        LatLng from = new LatLng("505310N", "0191209E");
        double bearing = LatLng.parseLongitude("0890118");
        double distance = 106000;

        LatLng destination = from.destination(bearing, distance);

        assertThat(destination.latitudeString()).isEqualTo("5053493N");
        assertThat(destination.longitudeString()).isEqualTo("02042758E");
    }

    @Test
    void latitudeString() {
        assertThat(latitudeString(0.0)).isEqualTo("0000000N");
        assertThat(latitudeString(90.0)).isEqualTo("9000000N");
        assertThat(latitudeString(45.5)).isEqualTo("4530000N");
        assertThat(latitudeString(-45.5)).isEqualTo("4530000S");
        assertThat(latitudeString(-45.76)).isEqualTo("4545600S");
        assertThat(latitudeString(-45.77)).isEqualTo("4546200S");
    }

    private String latitudeString(double latitude) {
        return new LatLng(latitude, 0.0).latitudeString();
    }

    @Test
    void longitudeString() {
        assertThat(longitudeString(0.0)).isEqualTo("00000000E");
        assertThat(longitudeString(90.0)).isEqualTo("09000000E");
        assertThat(longitudeString(45.5)).isEqualTo("04530000E");
        assertThat(longitudeString(-45.5)).isEqualTo("04530000W");
        assertThat(longitudeString(-45.76)).isEqualTo("04545600W");
        assertThat(longitudeString(-45.77)).isEqualTo("04546200W");
    }

    private String longitudeString(double longitude) {
        return new LatLng(0.0, longitude).longitudeString();
    }

}
