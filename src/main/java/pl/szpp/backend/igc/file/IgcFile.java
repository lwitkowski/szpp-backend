package pl.szpp.backend.igc.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.http.util.TextUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class IgcFile {

    private static final int MARKER_TAKE_OFF_HEIGHT = 10;

    public final TaskDeclaration declaration;
    @JsonIgnore public final List<Fix> track;

    public final String pilotInCharge;
    public final String gliderId;
    public final String gliderType;
    public final String competitionClass;
    public final String competitionId;
    public final String recorderType;
    public final LocalDate date;

    public final int startAltitude;
    public final int maxAltitude;
    public final int minAltitude;
    public final Fix takeOffFix;
    public final Fix landingFix;

    public IgcFile(TaskDeclaration declaration, List<Fix> fixes, String pilotInCharge, String gliderId, String gliderType,
                   String competitionClass, String competitionId, String recorderType, LocalDate date) {

        this.declaration = declaration;
        this.track = List.copyOf(fixes);

        this.pilotInCharge = pilotInCharge;
        this.gliderId = gliderId;
        this.gliderType = gliderType;
        this.competitionClass = competitionClass;
        this.competitionId = competitionId;
        this.recorderType = recorderType;
        this.date = date;

        int tmpMaxAltitude = -10000;
        int tmpMinAltitude = 10000;
        Fix tmpTakeOffFix = null;

        Fix firstFix = track.get(0);
        for (Fix fix : track) {
            tmpMaxAltitude = Math.max(fix.getAltitude(), tmpMaxAltitude);
            tmpMinAltitude = Math.min(fix.getAltitude(), tmpMinAltitude);

            if (tmpTakeOffFix == null && (fix.getAltitude() - firstFix.getAltitude() >= MARKER_TAKE_OFF_HEIGHT)) {
                tmpTakeOffFix = fix;
            }
        }
        this.minAltitude = tmpMinAltitude;
        this.maxAltitude = tmpMaxAltitude;
        this.takeOffFix = tmpTakeOffFix;

        this.startAltitude = firstFix.getAltitude();
        this.landingFix = (track.get(track.size() - 1));
    }

    public static IgcFile.Builder builder() {
        return new Builder();
    }

    public LocalTime getTakeOffTime() {
        return takeOffFix.time;
    }

    public LocalTime getLandingTime() {
        return landingFix.time;
    }

    public Duration getFlightTime() {
        return (takeOffFix != null && landingFix != null) ? Duration.between(takeOffFix.time, landingFix.time) : Duration.ZERO;
    }

    public String getGliderTypeAndId() {
        if (TextUtils.isEmpty(gliderId) && TextUtils.isEmpty(gliderType)) {
            return "";
        }

        if (TextUtils.isEmpty(gliderType)) {
            return gliderId;
        } else {
            if (!TextUtils.isEmpty(gliderId)) {
                String gliderInformationPlaceholder = "%1$s (%2$s)";
                return String.format(gliderInformationPlaceholder, gliderType, gliderId);
            } else {
                return gliderType;
            }
        }
    }
    public int getTakeOffAltitude() {
        return track.get(0).getAltitude();
    }

    public int getLandingAltitude() {
        return landingFix.getAltitude();
    }

    @Override
    public String toString() {
        return "IgcFile { fixes: " + track.size() + ", "
            + "waypoints: " + declaration.countWaypoints() + ", "
            + "maxAltitude: " + maxAltitude + "m, "
            + "minAltitude: " + minAltitude + "m, "
            + "takeOff: " + takeOffFix.time + ", "
            + "landing: " + landingFix.time + ""
            + " }";
    }


    public static class Builder {

        private String pilotInCharge;
        private String gliderId;
        private String gliderType;
        private String competitionClass;
        private String competitionId;
        private String recorderType;
        private String date;
        private final List<WayPoint> waypoints = new ArrayList<>();
        private final List<Fix> track = new ArrayList<>();

        private Builder() {
        }

        public void appendTrackPoint(Fix fix) {
            track.add(fix);
        }

        public void appendWayPoint(WayPoint waypoint) {
            waypoints.add(waypoint);
        }

        public IgcFile build() {
            if (track.isEmpty()) {
                throw new InvalidIgcException("No valid fixes found");
            }

            TaskDeclaration declaration = new TaskDeclaration(waypoints);
            if (!declaration.isValid()) {
                throw new InvalidIgcException("No valid declaration records found");
            }

            return new IgcFile(declaration, track, pilotInCharge, gliderId, gliderType, competitionClass, competitionId,
                recorderType, parseDate());
        }

        private LocalDate parseDate() {
            if (date == null) {
                throw new InvalidIgcException("Invalid format or missing date record");
            }
            return LocalDate.of(
                Integer.parseInt("20" + date.substring(4)),
                Integer.parseInt(date.substring(2, 4)),
                Integer.parseInt(date.substring(0, 2))
            );
        }

        public Builder withDate(String date) {
            this.date = date;
            return this;
        }

        public Builder withGliderType(String gliderType) {
            this.gliderType = gliderType;
            return this;
        }

        public Builder withGliderId(String gliderId) {
            this.gliderId = gliderId;
            return this;
        }

        public Builder withPilotInCharge(String pilotInCharge) {
            this.pilotInCharge = pilotInCharge;
            return this;
        }

        public Builder withRecorderType(String value) {
            this.recorderType = value;
            return this;
        }

        public Builder withCompetitionId(String value) {
            this.competitionId = value;
            return this;
        }

        public Builder withCompetitionClass(String value) {
            this.competitionClass = value;
            return this;
        }

    }

}
