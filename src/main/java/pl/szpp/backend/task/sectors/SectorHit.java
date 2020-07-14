package pl.szpp.backend.task.sectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.LatLng;

import java.time.LocalTime;

public class SectorHit extends LatLng {

    public static final SectorHit EMPTY = new SectorHit();

    public final LocalTime time;
    @JsonIgnore public final Fix lastBefore;
    @JsonIgnore public final Fix firstAfter;

    private SectorHit() {
        super(0.0, 0.0);
        this.time = null;
        this.lastBefore = null;
        this.firstAfter = null;
    }

    public SectorHit(LatLng hit, Fix lastBefore, Fix firstAfter) {
        super(hit.latitude, hit.longitude);
        this.time = interpolateHitTime(lastBefore, firstAfter, hit);
        this.lastBefore = lastBefore;
        this.firstAfter = firstAfter;
    }

    public static LocalTime interpolateHitTime(Fix lastBefore, Fix firstAfter, LatLng hit) {
        double previousToIntersectionDistanceRatio = lastBefore.distanceTo(hit) / lastBefore.distanceTo(firstAfter);
        int fixesTimeDifferenceSeconds = firstAfter.time.toSecondOfDay() - lastBefore.time.toSecondOfDay();
        int previousToIntersectionSeconds = (int) Math.ceil(fixesTimeDifferenceSeconds * previousToIntersectionDistanceRatio);
        return LocalTime.ofSecondOfDay(lastBefore.time.toSecondOfDay() + previousToIntersectionSeconds);
    }

    @Override
    public String toString() {
        return "SectorHit{" + time + ", " + latitudeString() + " " + longitudeString() + "}";
    }

}
