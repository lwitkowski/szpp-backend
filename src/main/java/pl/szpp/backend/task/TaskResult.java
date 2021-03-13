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
    public final double distance;

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

            double distanceTmp = 0.0;
            WayPoint prev = reachedWayPoints.get(0);
            for(int i = 1; i < reachedWayPoints.size(); ++i) {
                WayPoint current = reachedWayPoints.get(i);
                distanceTmp += prev.distanceTo(current);
                prev = current;
            }
            this.distance = distanceTmp - 2000;
        } else {
            this.startTime = null;
            this.finishTime = null;
            this.duration = null;
            this.distance = 0.0;
        }
    }

    @JsonProperty
    public boolean isCompleted() {
        return this.igc.declaration.fromStartToFinish().stream()
            .allMatch(bestHits::containsKey);
    }

    @JsonProperty
    public double averageSpeed() {
        return isCompleted() ? 3.6 * distance / duration.toSeconds() : 0.0;
    }

    @JsonProperty
    public List<WaypointHit> reachedWaypoints() {
        return bestHits.entrySet().stream()
            .map(entry -> new WaypointHit(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @JsonProperty
    public BitnerResult bitner() {
        return new BitnerResult("TRI", "Pegase");
    }

    private WayPoint lastReachedWp() {
        return reachedWayPoints.get(reachedWayPoints.size() - 1);
    }

    public class BitnerResult {

        private final Map<String, Double> taskCoefficients = Map.of(
            "3TP", 0.95,
            "TRI", 1.0,
            "FAI_TRI", 1.05
        );
        private final Map<String, Double> gliderHandicaps = Map.of(
            "Pegase", 0.96,
            "JS1_18m", 0.816
        );

        public final int score;
        public final double taskCoefficient;
        public final double gliderHandicap;

        public BitnerResult(String taskType, String gliderType) {
            this.taskCoefficient = taskCoefficients.getOrDefault(taskType, 1.0);
            this.gliderHandicap = gliderHandicaps.getOrDefault(gliderType, 1.0);

            if (isCompleted()) {
                // P=(10xL+50xVrz)xfk xfs
                this.score = (int) Math.round((10 * distance / 1000.0 + 50 * averageSpeed()) * taskCoefficient * gliderHandicap);
            } else {
                this.score = 0;
            }

        }

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
