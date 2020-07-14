package pl.szpp.backend.task.sectors;

import pl.szpp.backend.igc.file.Fix;

import java.util.Optional;

public interface WayPointSector {

    Optional<SectorHit> checkHit(Fix from, Fix to);

}
