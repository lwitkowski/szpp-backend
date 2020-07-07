package pl.szpp.backend.igc.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;

public class IgcParser {

    private static Logger logger = LoggerFactory.getLogger(IgcParser.class);

    public IgcFile parse(InputStream inputStream) {
        IgcFile igcFile = new IgcFile();

        try (InputStreamReader isReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(isReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (Fix.isBRecord(line)) {
                    igcFile.appendTrackPoint(new Fix(line));
                } else if (WayPoint.isCRecord(line)) {
                    igcFile.appendWayPoint(new WayPoint(line));
                } else {
                    GeneralField.setIfMatch(igcFile, line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        igcFile.finish();
        return igcFile;
    }

    private enum GeneralField {
        PILOT("HFPLTPILOT:", IgcFile::setPilotInCharge),
        PILOT_IN_CHARGE("HFPLTPILOTINCHARGE:", IgcFile::setPilotInCharge),
        GLIDER_ID("HFGIDGLIDERID:", IgcFile::setGliderId),
        GLIDER_TYPE("HFGTYGLIDERTYPE:", IgcFile::setGliderType),
        FLIGHT_DATE("HFDTE", IgcFile::setDate),
        RECORDER_TYPE("HFFTYFRTYPE:", IgcFile::setRecorderType),
        COMP_ID("HFCIDCOMPETITIONID:", IgcFile::setCompetitionId),
        COMP_CLASS("HFCCLCOMPETITIONCLASS:", IgcFile::setCompetitionClass);

        private final String prefix;
        private final BiConsumer<IgcFile, String> igcFileSetter;

        GeneralField(String prefix, BiConsumer<IgcFile, String> igcFileSetter) {
            this.prefix = prefix;
            this.igcFileSetter = igcFileSetter;
        }

        public static boolean setIfMatch(IgcFile file, String line) {
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
