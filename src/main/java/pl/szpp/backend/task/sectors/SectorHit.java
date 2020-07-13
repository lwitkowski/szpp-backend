package pl.szpp.backend.task.sectors;

import pl.szpp.backend.igc.file.Fix;
import pl.szpp.backend.igc.file.LatLng;

import java.time.LocalTime;

public class SectorHit extends LatLng {

    public final WayPointSector sector;
    public final LocalTime time;
    public final Fix lastBefore;
    public final Fix firstAfter;

    public SectorHit(WayPointSector sector, LatLng hit, Fix lastBefore, Fix firstAfter) {
        super(hit.latitude, hit.longitude);
        this.sector = sector;
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
}
