package pl.szpp.backend.igc;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/igc")
@RequestScoped
public class IgcResource {

    @Inject
    IgcUploadService uploadService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(description = "Uploads IGC file binaries")
    public Response upload(@RequestBody(content = @Content(mediaType = "multipart/form-data",
        schema = @Schema(allOf = {MultipartFormSchema.class}))) MultipartFormDataInput input) {
        uploadService.upload(input);
        return Response.ok().build();
    }

    @Schema(name = "MultipartFormSchema")
    static class MultipartFormSchema {

        @Schema(required = true, format = "binary", requiredProperties = "filename")
        public String file;

    }

}
