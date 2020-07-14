package pl.szpp.backend.igc.file;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TaskDeclaration {

    private final List<WayPoint> waypoints;

    public TaskDeclaration(List<WayPoint> waypoints) {
        this.waypoints = List.copyOf(waypoints);
    }

    public WayPoint getTakeoff() {
        return waypoints.get(1);
    }

    public WayPoint getStart() {
        return waypoints.get(2);
    }

    public WayPoint getFinish() {
        return waypoints.get(waypoints.size() - 2);
    }

    public WayPoint getLanding() {
        return waypoints.get(waypoints.size() - 1);
    }

    public List<WayPoint> fromStartToFinish() {
        return waypoints.stream()
            .skip(2)
            .limit(waypoints.size() - 3)
            .collect(toList());
    }

    public List<WayPoint> getTurnPoints() {
        return waypoints.stream()
            .skip(3)
            .limit(waypoints.size() - 5)
            .collect(toList());
    }

    public WayPoint getFirstAfterStart() {
        return waypoints.get(3);
    }

    public WayPoint getLastBeforeFinish() {
        return waypoints.get(waypoints.size() - 3);
    }

    public int countWaypoints() {
        return waypoints.size();
    }

    public boolean isValid() {
        return waypoints.size() > 5;
    }

    public List<WayPoint> getWaypoints() {
        return waypoints;
    }

    public double distance() {
        double distance = 0.0d;
        int i = 1;

        List<WayPoint> startToFinish = fromStartToFinish();
        while (i < startToFinish.size()) {
            WayPoint previous = startToFinish.get(i - 1);
            WayPoint current = startToFinish.get(i);
            distance += previous.distanceTo(current);
            ++i;
        }
        return distance / 1000.0d;
    }

}
