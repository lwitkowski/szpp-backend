package pl.szpp.backend.task;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import pl.szpp.backend.igc.IgcService;
import pl.szpp.backend.igc.file.IgcFile;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/task")
@RequestScoped
public class TaskResource {

    @Inject
    IgcService uploadService;

    @POST
    @Path("/calculate")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Calculates IGC file binaries")
    public CalculationResult upload(@RequestBody(content = @Content(mediaType = "multipart/form-data",
        schema = @Schema(allOf = {MultipartFormSchema.class}))) MultipartFormDataInput input) {
        IgcFile igc = uploadService.parse(input);
        Map<String, TaskResult> results = Map.of(
            BasicDeclaredTask.class.getSimpleName(), new BasicDeclaredTask().calculate(igc)
        );
        return new CalculationResult(igc, results);
    }

    @Schema(name = "MultipartFormSchema")
    static class MultipartFormSchema {

        @Schema(required = true, format = "binary", requiredProperties = "filename")
        public String file;

    }

    public static class CalculationResult {
        public final IgcFile igc;
        public final Map<String, TaskResult> results;

        public CalculationResult(IgcFile igc, Map<String, TaskResult> results) {
            this.igc = igc;
            this.results = results;
        }

    }

}
