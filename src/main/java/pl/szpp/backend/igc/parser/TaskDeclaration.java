package pl.szpp.backend.igc.parser;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TaskDeclaration {

    private final List<WayPoint> waypoints = new ArrayList<>();

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

    public List<WayPoint> getTurnPoints() {
        return waypoints.stream()
            .skip(3)
            .limit(waypoints.size() - 5)
            .collect(toList());
    }

    public void appendWayPoint(WayPoint waypoint) {
        waypoints.add(waypoint);
    }

    public int wpCount() {
        return waypoints.size();
    }

    public boolean isValid() {
        return waypoints.size() > 5;
    }

}
