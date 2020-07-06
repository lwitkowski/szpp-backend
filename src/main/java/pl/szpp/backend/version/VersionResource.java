package pl.szpp.backend.version;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/version")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class VersionResource {

    @ConfigProperty(name = "git.build.version", defaultValue = "n/a")
    String version;

    @ConfigProperty(name = "git.build.time", defaultValue = "n/a")
    String buildDate;

    @ConfigProperty(name = "git.commit.id.abbrev", defaultValue = "n/a")
    String revision;

    @GET
    @Operation(description = "Returns application name, version and git revision")
    public VersionResponse version() {
        return new VersionResponse("szpp-backend", version, buildDate, revision);
    }

}
