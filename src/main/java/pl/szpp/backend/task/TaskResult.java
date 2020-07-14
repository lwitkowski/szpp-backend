package pl.szpp.backend.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.szpp.backend.igc.file.IgcFile;
import pl.szpp.backend.igc.file.LatLng;
import pl.szpp.backend.igc.file.WayPoint;
import pl.szpp.backend.task.sectors.SectorHit;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskResult {

    public static final TaskResult EMPTY = new TaskResult(null, List.of(), Map.of());

    private final IgcFile igc;
    private final List<WayPoint> reachedWayPoints;
    private final Map<WayPoint, SectorHit> bestHits;

    public final LocalTime startTime;
    public final LocalTime finishTime;
    public final Duration duration;

    public TaskResult(IgcFile igc, List<WayPoint> reachedWayPoints, Map<WayPoint, SectorHit> bestHits) {
        this.igc = igc;
        this.reachedWayPoints = reachedWayPoints;
        this.bestHits = bestHits;

        if (igc != null) {
            SectorHit startWpHit = bestHits.getOrDefault(reachedWayPoints.get(0), SectorHit.EMPTY);
            SectorHit lastWpHit = bestHits.getOrDefault(lastReachedWp(), SectorHit.EMPTY);

            this.startTime = startWpHit.time;
            this.finishTime = lastWpHit.time;
            this.duration = Duration.between(startWpHit.time, lastWpHit.time).abs();
        } else {
            this.startTime = null;
            this.finishTime = null;
            this.duration = null;
        }
    }

    @JsonProperty
    public boolean isCompleted() {
        return this.igc.declaration.fromStartToFinish().stream()
            .allMatch(bestHits::containsKey);
    }

    @JsonProperty
    public double averageSpeed() {
        return isCompleted() ? (igc.declaration.distance() - 2.0) / (duration.toSeconds() / 3600.0) : 0.0;
    }

    @JsonProperty
    public List<WaypointHit> reachedWaypoints() {
        return bestHits.entrySet().stream()
            .map(entry -> new WaypointHit(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    // P=(10xL+50xVrz)xfk xfs
    @JsonProperty
    public int bitnerScore() {
        if(!isCompleted()) {
            return 0;
        }

        double distance = igc.declaration.distance() - 2.0;
        double taskCoefficient = 1.05;
        double gliderHandicap = 0.96;
        return (int) Math.round((10 * distance + 50 * averageSpeed()) * taskCoefficient * gliderHandicap);
    }

    private WayPoint lastReachedWp() {
        return reachedWayPoints.get(reachedWayPoints.size() - 1);
    }

    public static class WaypointHit extends LatLng {

        public final LocalTime time;
        public final String description;

        public WaypointHit(WayPoint wp, SectorHit hit) {
            super(hit.latitude, hit.longitude);
            this.time = hit.time;
            this.description = wp.description;
        }

    }

}
