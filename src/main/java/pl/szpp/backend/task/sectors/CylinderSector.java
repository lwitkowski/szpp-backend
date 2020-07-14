package pl.szpp.backend.task.sectors;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.LatLng;

import java.util.Optional;

public class CylinderSector implements WayPointSector {

    private final LatLng center;
    private final int radius;

    public CylinderSector(LatLng center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Optional<SectorHit> checkHit(Fix from, Fix to) {
        return (center.distanceTo(to) < radius) ? Optional.of(new SectorHit(to, from, to)) : Optional.empty();
    }

}
