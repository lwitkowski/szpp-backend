package pl.szpp.backend.igc;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.szpp.backend.igc.file.IgcFile;
import pl.szpp.backend.igc.file.IgcParser;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class IgcService {

    private static final Logger logger = LoggerFactory.getLogger(IgcService.class);

    @ConfigProperty(name = "igc.upload.dir")
    String uploadDirectory;

    private IgcParser igcParser = new IgcParser();

    @PostConstruct
    void postConstruct() {
        File dir = new File(uploadDirectory);
        dir.mkdirs();

        logger.info("IGC file storage location: {}", dir.getAbsolutePath());
    }

    void upload(MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");
        if (inputParts == null || inputParts.size() != 1) {
            throw new BadRequestException("file multipart form data not found");
        }

        InputPart inputPart = inputParts.get(0);
        String fileName = getFileName(inputPart);
        try {
            byte[] bytes = IOUtils.toByteArray(inputPart.getBody(InputStream.class, null));

            writeFile(bytes, fileName);

            logger.info("File saved as {}, size: {}kB", fileName, bytes.length >> 10);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public IgcFile parse(MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");
        if (inputParts == null || inputParts.size() != 1) {
            throw new BadRequestException("file multipart form data not found");
        }

        InputPart inputPart = inputParts.get(0);
        IgcFile igcFile;
        try {
            igcFile = igcParser.parse(inputPart.getBody(InputStream.class, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return igcFile;
    }

    /**
     * Content-Disposition: form-data; name = file; filename = 953LNF91.IGC
     * Content-Type: application/octet-stream
     **/
    private String getFileName(InputPart inputPart) {
        MultivaluedMap<String, String> header = inputPart.getHeaders();
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }
        throw new BadRequestException("filename header not found for file part");
    }

    private void writeFile(byte[] content, String filename) throws IOException {
        File file = new File(uploadDirectory + filename);

        if (file.exists()) {
            //throw new WebApplicationException("File exists", Response.Status.CONFLICT);
            file.delete();
        }

        file.createNewFile();
        try (FileOutputStream fop = new FileOutputStream(file)) {
            fop.write(content);
            fop.flush();
        }
    }

}
