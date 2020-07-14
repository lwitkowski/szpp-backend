package pl.szpp.backend.task.sectors;

import org.locationtech.jts.geom.LineSegment;
import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.LatLng;
import pl.szpp.backend.igc.file.WayPoint;

import java.util.Optional;

public class LineSector implements WayPointSector {

    public static LineSector forRoute(LatLng center, int widthMeters, WayPoint next) {
        double latDiff = next.latitude - center.latitude;

        double northMean = (center.latitude + next.latitude) * Math.PI / 360;
        double startRads = center.latitude * Math.PI / 180;
        double longDiff = (center.longitude - next.longitude) * Math.cos(northMean);
        double hypotenuse = Math.sqrt(latDiff * latDiff + longDiff * longDiff);

        int lineRadiusKm = widthMeters / 1000;
        double latDelta = lineRadiusKm * longDiff / hypotenuse / 111.1949269;
        double longDelta = lineRadiusKm * latDiff / hypotenuse / 111.1949269 / Math.cos(startRads);
        return new LineSector(
            new LatLng(center.latitude - latDelta, center.longitude - longDelta),
            new LatLng(center.latitude + latDelta, longDelta + center.longitude)
        );
    }

    private final LatLng start;
    private final LatLng end;

    public LineSector(LatLng start, LatLng end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Optional<SectorHit> checkHit(Fix from, Fix to) {
        LineSegment line = new LineSegment(start.latitude, start.longitude, end.latitude, end.longitude);
        LineSegment fixesLine = new LineSegment(from.latitude, from.longitude, to.latitude, to.longitude);
        return Optional.ofNullable(line.intersection(fixesLine))
            .map(coordinate -> new SectorHit(new LatLng(coordinate.x, coordinate.y), from, to));
    }

}
