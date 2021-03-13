package pl.szpp.backend.task.sectors;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.LatLng;

import java.util.Optional;

public class CylinderSector implements WayPointSector {

    private final LatLng center;
    private final double radius;

    public CylinderSector(LatLng center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Optional<SectorHit> checkHit(Fix from, Fix to) {
        if(radius == 500.0 && center.distanceTo(to) < 1000) {
            System.out.println(center.latitudeString() + center.longitudeString() + " to " + to.latitudeString() + to.longitudeString() + " = " + center.distanceTo(to));
        }
        return (center.distanceTo(to) < radius) ? Optional.of(new SectorHit(to, from, to)) : Optional.empty();
    }

}
