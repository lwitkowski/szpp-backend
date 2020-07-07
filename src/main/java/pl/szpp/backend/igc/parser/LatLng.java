package pl.szpp.backend.igc.parser;

import java.util.List;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static pl.szpp.backend.igc.parser.IgcFile.isZero;

class LatLng {
    public final double latitude;
    public final double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    static final double EARTH_RADIUS = 6371009;

    public static double parseLatitude(String lat) {
        double latDegrees = Double.parseDouble(lat.substring(0, 2));
        String latMinutes = lat.substring(2, 4);
        String latSeconds = lat.substring(4, lat.length() - 1);
        double latRealMinutes = Double.parseDouble(latMinutes + "." + latSeconds) / 60;
        double latDecimalFormat = latDegrees + latRealMinutes;
        if (hasSuffix(lat, 'S')) {
            latDecimalFormat = -1 * latDecimalFormat;
        }

        return latDecimalFormat;
    }

    public static double parseLongitude(String lon) {
        double lonDegrees = Double.parseDouble(lon.substring(0, 3));
        String lonMinutes = lon.substring(3, 5);
        String lonSeconds = lon.substring(5, lon.length() - 1);
        double lonRealMinutes = Double.parseDouble(lonMinutes + "." + lonSeconds) / 60;
        double lonDecimalFormat = lonDegrees + lonRealMinutes;
        if (hasSuffix(lon, 'W')) {
            lonDecimalFormat = -1 * lonDecimalFormat;
        }
        return lonDecimalFormat;
    }

    private static boolean hasSuffix(String lat, char c) {
        return lat.charAt(lat.length() - 1) == c;
    }

    public static boolean isZeroCoordinate(WayPoint wayPoint) {
        return wayPoint == null
            || (isZero(wayPoint.latitude) && isZero(wayPoint.longitude));
    }

    public static double distanceBetween(double lat, double lon, double lat1, double lon1) {
        return computeAngleBetween(new LatLng(lat, lon), new LatLng(lat1, lon1)) * EARTH_RADIUS;
    }

    /**
     * Returns distance on the unit sphere; the arguments are in radians.
     */
    private static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }

    /**
     * Returns the angle between two LatLngs, in radians. This is the same as the distance
     * on the unit sphere.
     */
    static double computeAngleBetween(LatLng from, LatLng to) {
        return distanceRadians(toRadians(from.latitude), toRadians(from.longitude),
            toRadians(to.latitude), toRadians(to.longitude));
    }
    /**
     * Returns the length of the given path, in meters, on Earth.
     */
    public static double computeLength(List<LatLng> path) {
        if (path.size() < 2) {
            return 0;
        }
        double length = 0;
        LatLng prev = path.get(0);
        double prevLat = toRadians(prev.latitude);
        double prevLng = toRadians(prev.longitude);
        for (LatLng point : path) {
            double lat = toRadians(point.latitude);
            double lng = toRadians(point.longitude);
            length += distanceRadians(prevLat, prevLng, lat, lng);
            prevLat = lat;
            prevLng = lng;
        }
        return length * EARTH_RADIUS;
    }


    /**
     * Returns haversine(angle-in-radians).
     * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
     */
    static double hav(double x) {
        double sinHalf = sin(x * 0.5);
        return sinHalf * sinHalf;
    }

    /**
     * Computes inverse haversine. Has good numerical stability around 0.
     * arcHav(x) == acos(1 - 2 * x) == 2 * asin(sqrt(x)).
     * The argument must be in [0, 1], and the result is positive.
     */
    static double arcHav(double x) {
        return 2 * asin(sqrt(x));
    }

    /**
     * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit sphere.
     */
    static double havDistance(double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2);
    }
}
