package pl.szpp.backend.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.szpp.backend.StopWatch;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class BasicDeclaredTask implements Task {

    public static final int START_LINE_METERS = 1000;
    public static final int FINISH_CYLiNDER_RADIUS_METERS = 2000;
    public static final int TURN_POINT_RADIUS_METERS = 500;

    private static final Logger logger = LoggerFactory.getLogger(BasicDeclaredTask.class);

    @Override
    public TaskResult calculate(IgcFile igc) {
        StopWatch t = new StopWatch();
        logger.info("Analyzing igc file with {} waypoints", igc.declaration.fromStartToFinish().size());
        Map<WayPoint, SectorHits> hits = findSectorHits(igc);
        logger.info("Found all sectors hits in {}ms", t.measure());

        if (hits.get(igc.declaration.getStart()).isEmpty()) {
            logger.debug("Start line not reached");
            return TaskResult.EMPTY;
        }

        List<WayPoint> reachedWayPoints = findReachedWayPoints(igc, hits);
        logger.info("Reached {} waypoints: {}", reachedWayPoints.size(), reachedWayPoints);

        WayPoint startWayPoint = reachedWayPoints.get(0);
        logger.info("First waypoint has {} hits, analysing all possible results", hits.get(startWayPoint).size());

        List<TaskResult> runsSorted = hits.get(startWayPoint).stream()
            .flatMap(startWpHit -> findBestRoute(startWpHit, hits, reachedWayPoints))
            .map(bestHits -> new TaskResult(igc, reachedWayPoints, bestHits))
            .sorted(Comparator.comparingLong(run -> run.duration.toMillis()))
            .collect(toList());

        logger.info("Found {} valid task runs in this flight with times {} in {}ms", runsSorted.size(),
            runsSorted.stream().map(result -> result.duration).collect(toList()), t.measure());
        if (runsSorted.isEmpty()) {
            return TaskResult.EMPTY;
        }
        logger.info("Best run ({}): {}", runsSorted.get(0).duration, runsSorted.get(0));
        return runsSorted.get(0);
    }

    private List<WayPoint> findReachedWayPoints(IgcFile igc, Map<WayPoint, SectorHits> hits) {
        List<WayPoint> reachedWayPoints = new ArrayList<>();
        LocalTime currentTime = LocalTime.ofSecondOfDay(0);
        for (WayPoint wp : igc.declaration.fromStartToFinish()) {
            Optional<SectorHit> hit = hits.get(wp).findFirstAtOrAfter(currentTime);
            if (hit.isPresent()) {
                reachedWayPoints.add(wp);
                currentTime = hit.get().time;
            } else {
               return reachedWayPoints;
            }
        }
        return reachedWayPoints;
    }

    private Stream<Map<WayPoint, SectorHit>> findBestRoute(SectorHit startWpHit,
                         Map<WayPoint, SectorHits> hits, List<WayPoint> reachedWayPoints) {
        logger.info("Analysing run starting at {}", startWpHit.time);

        Map<WayPoint, SectorHit> bestHits = new LinkedHashMap<>();
        LocalTime currentT = startWpHit.time;
        for (WayPoint wp : reachedWayPoints) {
            SectorHits wpHits = hits.get(wp);
            Optional<SectorHit> wpFirstHit = wpHits.findFirstAtOrAfter(currentT);
            if (wpFirstHit.isPresent()) {
                bestHits.put(wp, wpFirstHit.get());
                currentT = wpFirstHit.get().time;
            } else {
                logger.info("No hit for {} before {}, discarding this run", wp, currentT);
                return Stream.empty();
            }
        }
        return Stream.of(bestHits);
    }


    private Map<WayPoint, SectorHits> findSectorHits(IgcFile igc) {
        return prepareSectors(igc.declaration).entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                entry -> findTimeSortedHits(igc, entry.getValue())
            ));
    }

    private Map<WayPoint, WayPointSector> prepareSectors(TaskDeclaration declaration) {
        Map<WayPoint, WayPointSector> sectors = new HashMap<>();

        sectors.put(
            declaration.getStart(),
            LineSector.forRoute(declaration.getStart(), START_LINE_METERS, declaration.getFirstAfterStart())
        );

        declaration.getTurnPoints().forEach(tp -> {
            sectors.put(tp, new CylinderSector(tp, TURN_POINT_RADIUS_METERS));
        });

        sectors.put(
            declaration.getFinish(),
            new CylinderSector(declaration.getFinish(), FINISH_CYLiNDER_RADIUS_METERS)
        );

        return sectors;
    }

    private SectorHits findTimeSortedHits(IgcFile igcFile, WayPointSector sector) {
        List<Fix> soaringTrack = igcFile.soaringTrack();
        SectorHits hits = new SectorHits();
        int i = 1;
        while (i < soaringTrack.size()) {
            Fix previous = soaringTrack.get(i - 1);
            Fix current = soaringTrack.get(i);
            sector.checkHit(previous, current)
                .ifPresent(hits::add);
            ++i;
        }
        hits.sort(Comparator.comparingLong(e -> e.time.toNanoOfDay()));
        return hits;
    }

    private static class SectorHits extends ArrayList<SectorHit> {

        public Optional<SectorHit> findFirstAtOrAfter(LocalTime time) {
            for (int i = 0; i < size(); ++i) {
                SectorHit currentHit = get(i);
                if (!currentHit.time.isBefore(time)) {
                    return Optional.of(currentHit);
                }
            }
            return Optional.empty();
        }

    }

}
