package pl.szpp.backend.igc;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class IgcResourceIT {

    private static final String FILENAME = "953LNF91.IGC";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @ConfigProperty(name = "igc.upload.dir")
    String uploadDirectory;

    @BeforeEach
    void setup() throws IOException {
        FileUtils.deleteDirectory(new File(uploadDirectory));
    }

    @AfterEach
    void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(uploadDirectory));
    }

    @Test
    void uploadShouldStoreIgcFile() {
        File originalFile = new File("src/test/resources/igc/" + FILENAME);
        given()
            .multiPart("file", originalFile)
            .when()
            .accept(ContentType.JSON)
            .post("/igc/upload")
            .then()
            .statusCode(200)
            .body("pilotInCharge", is("LUKASZ___WITKOWSKI"));

        File uploadedFile = new File(uploadDirectory + FILENAME);
        assertThat(uploadedFile.exists()).isTrue();
        assertThat(uploadedFile).hasSameContentAs(originalFile);
    }

}
