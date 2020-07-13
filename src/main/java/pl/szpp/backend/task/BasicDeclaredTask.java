package pl.szpp.backend.task;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.IgcFile;
import pl.szpp.backend.igc.file.TaskDeclaration;
import pl.szpp.backend.igc.file.WayPoint;
import pl.szpp.backend.task.sectors.CylinderSector;
import pl.szpp.backend.task.sectors.LineSector;
import pl.szpp.backend.task.sectors.SectorHit;
import pl.szpp.backend.task.sectors.WayPointSector;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class BasicDeclaredTask implements Task {

    public static final int START_LINE_METERS = 1000;
    public static final int FINISH_LINE_METERS = 1000;
    public static final int TURN_POINT_RADIUS_METERS = 500;

    @Override
    public TaskResult calculate(IgcFile igc) {
        TaskDeclaration declaration = igc.declaration;
        List<WayPointSector> sectors = prepareSectors(declaration);
        Map<WayPoint, SectorHit> visits = visitSectors(sectors, igc);

        LocalTime startTime = visits.containsKey(declaration.getStart())
            ? visits.get(declaration.getStart()).time : null;

        return isCompleted(declaration, visits)
            ? TaskResult.completed(igc, startTime, visits.get(declaration.getFinish()).time)
            : TaskResult.notCompleted(igc, startTime, igc.track.get(igc.track.size() - 1));
    }

    private List<WayPointSector> prepareSectors(TaskDeclaration declaration) {
        List<WayPointSector> sectors = new ArrayList<>();

        sectors.add(LineSector.forRoute(declaration.getStart(), START_LINE_METERS, declaration.getFirstAfterStart()));

        sectors.addAll(declaration.getTurnPoints().stream()
            .map(tp -> new CylinderSector(tp, TURN_POINT_RADIUS_METERS))
            .collect(Collectors.toList()));

        sectors.add(LineSector.forRoute(declaration.getFinish(), FINISH_LINE_METERS, declaration.getLastBeforeFinish()));

        return sectors;
    }

    private Map<WayPoint, SectorHit> visitSectors(List<WayPointSector> sectors, IgcFile igc) {
        AtomicInteger lastHitIndex = new AtomicInteger(0);
        return sectors.stream()
            .flatMap(sector -> {
                List<Fix> trackFromLastHit = igc.track.subList(lastHitIndex.get(), igc.track.size());
                Optional<SectorHit> hit = sector.findFirstHit(trackFromLastHit);
                hit.ifPresent(h -> lastHitIndex.set(igc.track.indexOf(h.firstAfter)));
                return hit.stream();
            })
            .collect(toMap(hit -> hit.sector.waypoint(), Function.identity()));
    }

    private boolean isCompleted(TaskDeclaration declaration, Map<WayPoint, SectorHit> visits) {
        return declaration.fromStartToFinish().stream()
            .allMatch(visits::containsKey);
    }

}
