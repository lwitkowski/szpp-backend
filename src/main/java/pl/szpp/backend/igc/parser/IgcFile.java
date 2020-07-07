package pl.szpp.backend.igc.parser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.http.util.TextUtils;

import javax.ws.rs.BadRequestException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class IgcFile {

    private static final int MARKER_TAKE_OFF_HEIGHT = 10;

    private String pilotInCharge;
    private String gliderId;
    private String gliderType;
    private String competitionClass;
    private String competitionId;
    private String recorderType;
    private LocalDate date;
    private final TaskDeclaration taskDeclaration = new TaskDeclaration();

    private final List<Fix> track = new ArrayList<>();

    private int startAltitude;
    private int maxAltitude = -10000;
    private int minAltitude = 10000;
    private LocalTime takeOffTime;
    private LocalTime landingTime;

    public void appendTrackPoint(Fix fix) {
        track.add(fix);
    }

    public void appendWayPoint(WayPoint waypoint) {
        taskDeclaration.appendWayPoint(waypoint);
    }

    @JsonIgnore
    public List<Fix> getTrack() {
        return track;
    }

    public TaskDeclaration getTaskDeclaration() {
        return taskDeclaration;
    }

    public int getMaxAltitude() {
        return maxAltitude;
    }

    public int getMinAltitude() {
        return this.minAltitude;
    }

    public LocalTime getTakeOffTime() {
        return takeOffTime;
    }

    public LocalTime getLandingTime() {
        return landingTime;
    }

    public Duration getFlightTime() {
        return (takeOffTime != null && landingTime != null) ? Duration.between(takeOffTime, landingTime) : Duration.ZERO;
    }

    public void setGliderType(String gliderType) {
        this.gliderType = gliderType;
    }

    public void setGliderId(String gliderId) {
        this.gliderId = gliderId;
    }

    public String getPilotInCharge() {
        return pilotInCharge;
    }

    public void setPilotInCharge(String pilotInCharge) {
        this.pilotInCharge = pilotInCharge;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String line) {
        this.date = LocalDate.of(
            Integer.parseInt("20" + line.substring(4)),
            Integer.parseInt(line.substring(2, 4)),
            Integer.parseInt(line.substring(0, 2))
        );
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

    public int getStartAltitude() {
        return startAltitude;
    }

    public int getTakeOffAltitude() {
        if (!track.isEmpty()) {
            return track.get(0).getAltitude();
        }
        return 0;
    }

    public int getLandingAltitude() {
        if (!track.isEmpty()) {
            return track.get(track.size() - 1).getAltitude();
        }
        return 0;
    }

    /**
     * Calculates flight stats
     * @return true if IGC file is valid
     */
    public void finish() {
        if (track.isEmpty() || !taskDeclaration.isValid()) {
            throw new BadRequestException("Invalid IGC file");
        }

        Fix firstFix = track.get(0);
        for (Fix fix : track) {
            maxAltitude = Math.max(fix.getAltitude(), maxAltitude);
            minAltitude = Math.min(fix.getAltitude(), minAltitude);

            if (takeOffTime == null && (fix.getAltitude() - firstFix.getAltitude() >= MARKER_TAKE_OFF_HEIGHT)) {
                takeOffTime = fix.getTime();
            }
        }
        this.startAltitude = (firstFix != null) ? firstFix.getAltitude() : 0;
        this.landingTime = (track.get(track.size() - 1)).getTime();
    }

    public static boolean isZero(double value) {
        return value >= -1 && value <= 0.1;
    }

    public void setRecorderType(String value) {
        this.recorderType = value;
    }

    public void setCompetitionId(String value) {
        this.competitionId = value;
    }

    public void setCompetitionClass(String value) {
        this.competitionClass = value;
    }

    public String getCompetitionClass() {
        return competitionClass;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public String getRecorderType() {
        return recorderType;
    }

    @Override
    public String toString() {
        return "IGCFile --- Track Points: "
            + track.size()
            + " :: amountTrackPoints: " + track.size()
            + " :: amountWayPoints: " + taskDeclaration.wpCount()
            + " :: maxAltitude: " + maxAltitude
            + " :: minAltitude: " + minAltitude
            + " :: landingTime: " + landingTime
            + " :: takeOffTime: " + takeOffTime;
    }

}
