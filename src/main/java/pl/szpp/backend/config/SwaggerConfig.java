package pl.szpp.backend.config;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Szybowcowy Puchar Polski - backend app",
        version = "0.1"),
    components = @Components(
        responses = {
            @APIResponse(
                name = "ResourceNotFound",
                description = "Resource not found"
            )
        })
)
class SwaggerConfig extends Application {

}
