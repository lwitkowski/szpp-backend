package pl.szpp.backend.task;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.IgcFileFixtures;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class TaskResourceIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void calculateShouldReturnBasicDeclaredTaskResult() {
        given()
            .multiPart("file", IgcFileFixtures.IGC_FILE)
            .when()
            .accept(ContentType.JSON)
            .post("/task/calculate")
            .then()
            .statusCode(200)
            .body("results.BasicDeclaredTask.completed", is(false))
            .body("results.BasicDeclaredTask.reachedWaypoints", hasSize(1));
    }

}
