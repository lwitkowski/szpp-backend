package pl.szpp.backend.task;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.szpp.backend.igc.file.IgcFileFixtures;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
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
            .body("results.BasicDeclaredTask.completed", is(true));
    }

}
