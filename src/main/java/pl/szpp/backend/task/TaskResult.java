package pl.szpp.backend.task;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.IgcFile;

import java.time.Duration;
import java.time.LocalTime;

public class TaskResult {

    public static TaskResult completed(IgcFile igc, LocalTime startTime, LocalTime finishTime) {
        return new TaskResult(igc, true, startTime, finishTime);
    }

    public static TaskResult notCompleted(IgcFile igc, LocalTime startTime, Fix virtualLanding) {
        return new TaskResult(igc, true, startTime, virtualLanding == null ? null : virtualLanding.time); // FIXME
    }

    private final IgcFile igc;
    public final boolean completed;
    public final LocalTime startTime;
    public final LocalTime finishTime;

    private TaskResult(IgcFile igc, boolean completed, LocalTime startTime, LocalTime finishTime) {
        this.igc = igc;
        this.completed = completed;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public Duration duration() {
        return startTime != null && finishTime != null ? Duration.between(startTime, finishTime) : Duration.ZERO;
    }

    public double averageSpeed() {
        return igc.declaration.distance() / (duration().toSeconds() / 3600.0);
    }

}
