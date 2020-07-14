package pl.szpp.backend.igc.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;

public class IgcParser {

    public IgcFile parse(InputStream inputStream) {
        IgcFile.Builder igcFile = IgcFile.builder();

        try (InputStreamReader isReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(isReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (Fix.isBRecord(line)) {
                    igcFile.appendTrackPoint(Fix.parseIgcLine(line));
                } else if (WayPoint.isCRecord(line)) {
                    igcFile.appendWayPoint(WayPoint.parseIgcLine(line));
                } else {
                    GeneralField.setIfMatch(igcFile, line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return igcFile.build();
    }

    private enum GeneralField {
        PILOT("HFPLTPILOT:", IgcFile.Builder::withPilotInCharge),
        PILOT_IN_CHARGE("HFPLTPILOTINCHARGE:", IgcFile.Builder::withPilotInCharge),
        GLIDER_ID("HFGIDGLIDERID:", IgcFile.Builder::withGliderId),
        GLIDER_TYPE("HFGTYGLIDERTYPE:", IgcFile.Builder::withGliderType),
        FLIGHT_DATE("HFDTEDATE:", IgcFile.Builder::withDate),
        FLIGHT_DATE_OLD_FORMAT("HFDTE", IgcFile.Builder::withDate),
        RECORDER_TYPE("HFFTYFRTYPE:", IgcFile.Builder::withRecorderType),
        COMP_ID("HFCIDCOMPETITIONID:", IgcFile.Builder::withCompetitionId),
        COMP_CLASS("HFCCLCOMPETITIONCLASS:", IgcFile.Builder::withCompetitionClass);

        private final String prefix;
        private final BiConsumer<IgcFile.Builder, String> igcFileSetter;

        GeneralField(String prefix, BiConsumer<IgcFile.Builder, String> igcFileSetter) {
            this.prefix = prefix;
            this.igcFileSetter = igcFileSetter;
        }

        public static boolean setIfMatch(IgcFile.Builder file, String line) {
            final String lineUpperCase = line.toUpperCase();

            for (GeneralField field : GeneralField.values()) {
                if (lineUpperCase.startsWith(field.prefix)) {
                    field.igcFileSetter.accept(file, field.getValueField(line));
                    return true;
                }
            }
            return false;
        }

        private String getValueField(String line) {
            return line.replaceAll(prefix, "").trim();
        }
    }

}
