package pl.szpp.backend.task.sectors;

import org.locationtech.jts.geom.LineSegment;
import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.LatLng;
import pl.szpp.backend.igc.file.WayPoint;

import java.util.List;
import java.util.Optional;

public class LineSector implements WayPointSector {

    public static LineSector forRoute(WayPoint center, int widthMeters, WayPoint next) {
        double latDiff = next.latitude - center.latitude;

        double northMean = (center.latitude + next.latitude) * Math.PI / 360;
        double startRads = center.latitude * Math.PI / 180;
        double longDiff = (center.longitude - next.longitude) * Math.cos(northMean);
        double hypotenuse = Math.sqrt(latDiff * latDiff + longDiff * longDiff);

        int lineRadiusKm = widthMeters / 1000;
        double latDelta = lineRadiusKm * longDiff / hypotenuse / 111.1949269;
        double longDelta = lineRadiusKm * latDiff / hypotenuse / 111.1949269 / Math.cos(startRads);
        return new LineSector(
            center,
            new LatLng(center.latitude - latDelta, center.longitude - longDelta),
            new LatLng(center.latitude + latDelta, longDelta + center.longitude)
        );
    }

    private final WayPoint center;
    private final LatLng start;
    private final LatLng end;

    public LineSector(WayPoint center, LatLng start, LatLng end) {
        this.center = center;
        this.start = start;
        this.end = end;
    }

    @Override
    public WayPoint waypoint() {
        return center;
    }


    @Override
    public Optional<SectorHit> findFirstHit(List<Fix> track) {
        int i = 1;
        while (i < track.size()) {
            Fix previous = track.get(i - 1);
            Fix current = track.get(i);
            Optional<LatLng> intersectionOptional = findIntersection(previous, current);
            if (intersectionOptional.isPresent()) {
                SectorHit hit = new SectorHit(this, intersectionOptional.get(), previous, current);
                return Optional.of(hit);
            }
            ++i;
        }
        return Optional.empty();
    }

    private Optional<LatLng> findIntersection(LatLng from, LatLng to) {
        LineSegment line = new LineSegment(start.latitude, start.longitude, end.latitude, end.longitude);
        LineSegment fixesLine = new LineSegment(from.latitude, from.longitude, to.latitude, to.longitude);
        return Optional.ofNullable(line.intersection(fixesLine))
            .map(coordinate -> new LatLng(coordinate.x, coordinate.y));
    }

}
