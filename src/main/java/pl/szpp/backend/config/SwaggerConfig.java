package pl.szpp.backend.config;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Szybowcowy Puchar Polski - backend app REST API",
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
