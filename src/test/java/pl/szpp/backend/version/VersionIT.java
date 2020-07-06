package pl.szpp.backend.version;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class VersionIT {

    @Test
    void versionShouldReturnApplicationAndVersion() {
        when()
            .get("/version")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .body("application", is("szpp-backend"))
            .body("version", is(notNullValue())).log().all();
    }

}
