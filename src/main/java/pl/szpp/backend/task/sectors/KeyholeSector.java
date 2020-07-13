package pl.szpp.backend.task.sectors;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.WayPoint;

import java.util.List;
import java.util.Optional;

public class KeyholeSector implements WayPointSector {

    private final WayPoint center;
    private final int smallRadius;
    private final int bigRadius;
    private final WayPoint previous;
    private final WayPoint next;

    public KeyholeSector(WayPoint center, int smallRadius, int bigRadius, WayPoint previous, WayPoint next) {
        this.center = center;
        this.smallRadius = smallRadius;
        this.bigRadius = bigRadius;
        this.previous = previous;
        this.next = next;
    }

    @Override
    public WayPoint waypoint() {
        return center;
    }

    @Override
    public Optional<SectorHit> findFirstHit(List<Fix> track) {
        for (Fix fix : track) {
            if (insideSmallCircle(fix) || insideCheese(fix)) {
                SectorHit hit = new SectorHit(this, fix, fix, fix);
                return Optional.of(hit);
            }
        }
        return Optional.empty();
    }

    private boolean insideSmallCircle(Fix fix) {
        return center.distanceTo(fix) < smallRadius;
    }

    private boolean insideCheese(Fix fix) {
        return false;
    }

}
