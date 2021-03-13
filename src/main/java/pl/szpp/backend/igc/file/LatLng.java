package pl.szpp.backend.igc.file;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class LatLng {

    public static final double EARTH_RADIUS_METERS = 6371000.0;

    public final double latitude;
    public final double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng(String latitudeString, String longitudeString) {
        this.latitude = parseLatitude(latitudeString);
        this.longitude = parseLongitude(longitudeString);
    }

    // 5206343N: <lat> i.e. 52 degrees 06.343 minutes North
    public static double parseLatitude(String lat) {
        String pureDigits = lat.replace("N", "").replace("S", "");
        double degrees = Double.parseDouble(pureDigits.substring(0, 2));
        double minutes = Double.parseDouble(pureDigits.substring(2, 4) + "." + pureDigits.substring(4));
        double latDecimalFormat = degrees + minutes / 60;
        if (hasSuffix(lat, 'S')) {
            latDecimalFormat *= -1;
        }

        return latDecimalFormat;
    }

    // 00006198W: <long> i.e. 000 degrees 06.198 minutes West
    public static double parseLongitude(String lon) {
        String pureDigits = lon.replace("W", "").replace("E", "");
        double degrees = Double.parseDouble(pureDigits.substring(0, 3));
        double minutes = Double.parseDouble(pureDigits.substring(3, 5) + "." + pureDigits.substring(5));
        double lonDecimalFormat = degrees + minutes / 60;
        if (hasSuffix(lon, 'W')) {
            lonDecimalFormat = -1 * lonDecimalFormat;
        }
        return lonDecimalFormat;
    }

    public String latitudeString() {
        double absValue = Math.abs(latitude);
        int d = (int) absValue;
        double m = ((absValue - d) * 60.0);
        return String.format("%02d%02d%03d", d, (int) m, (int) Math.round((m - (int) m) * 1000)) + ((latitude >= 0) ? "N" : "S");
    }

    public String longitudeString() {
        double absValue = Math.abs(longitude);
        int d = (int) absValue;
        double m = ((absValue - d) * 60.0);
        return String.format("%03d%02d%03d", d, (int) m, (int) Math.round((m - (int) m) * 1000)) + ((longitude >= 0) ? "E" : "W");
    }

    private static boolean hasSuffix(String lat, char c) {
        return lat.charAt(lat.length() - 1) == c;
    }

    /**
     * Destination point given distance and bearing from start point
     * @param bearing clockwise from north in degrees
     * @param distance meters
     * @return
     */
    public LatLng destination(double bearing, double distance) {
        double φ1 = toRadians(latitude);
        double λ1 = toRadians(longitude);
        double θ = toRadians(bearing);

        double angularDistance = distance / EARTH_RADIUS_METERS;
        double lat2 = Math.asin(
            Math.sin(φ1) * Math.cos(angularDistance) + Math.cos(φ1) * Math.sin(angularDistance) * Math.cos(θ)
        );
        double lon2 = λ1 + Math.atan2(
            Math.sin(θ) * Math.sin(angularDistance) * Math.cos(φ1),
            Math.cos(angularDistance) - Math.sin(φ1) * Math.sin(lat2)
        );
        return new LatLng(toDegrees(lat2), toDegrees(lon2));
    }

    /**
     * @return Distance in meters between this and to points on earth
     */
    public double distanceTo(LatLng to) {
        return computeAngleBetween(this, to) * EARTH_RADIUS_METERS;
    }


    /**
     * Returns the angle between two LatLngs, in radians. This is the same as the distance
     * on the unit sphere.
     */
    private static double computeAngleBetween(LatLng from, LatLng to) {
        return distanceRadians(toRadians(from.latitude), toRadians(from.longitude),
            toRadians(to.latitude), toRadians(to.longitude));
    }

    /**
     * Returns distance on the unit sphere; the arguments are in radians.
     */
    private static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }

    /**
     * Computes inverse haversine. Has good numerical stability around 0.
     * arcHav(x) == acos(1 - 2 * x) == 2 * asin(sqrt(x)).
     * The argument must be in [0, 1], and the result is positive.
     */
    private static double arcHav(double x) {
        return 2 * asin(sqrt(x));
    }

    /**
     * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit sphere.
     */
    private static double havDistance(double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2);
    }

    /**
     * Returns haversine(angle-in-radians).
     * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
     */
    private static double hav(double x) {
        double sinHalf = sin(x * 0.5);
        return sinHalf * sinHalf;
    }
}
