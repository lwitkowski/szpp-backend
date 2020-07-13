package pl.szpp.backend.task.sectors;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.WayPoint;

import java.util.List;
import java.util.Optional;

public class CylinderSector implements WayPointSector {

    private final WayPoint center;
    private final int radius;

    public CylinderSector(WayPoint center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public WayPoint waypoint() {
        return center;
    }

    @Override
    public Optional<SectorHit> findFirstHit(List<Fix> track) {
        for (Fix fix : track) {
            if (center.distanceTo(fix) < radius) {
                SectorHit hit = new SectorHit(this, fix, fix, fix);
                return Optional.of(hit);
            }
        }
        return Optional.empty();
    }

}
