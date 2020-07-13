package pl.szpp.backend.task.sectors;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.WayPoint;

import java.util.List;
import java.util.Optional;

public interface WayPointSector {

    WayPoint waypoint();

    Optional<SectorHit> findFirstHit(List<Fix> track);

}
