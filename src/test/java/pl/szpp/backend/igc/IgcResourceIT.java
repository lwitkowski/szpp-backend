package pl.szpp.backend.igc;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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
        given()
            .multiPart("file", new File("src/test/resources/igc/" + FILENAME))
            .when()
            .accept(ContentType.JSON)
            .post("/igc/upload")
            .then()
            .statusCode(200);

        File file = new File(uploadDirectory + FILENAME);
        assertThat(file.exists()).isTrue();
    }

}
