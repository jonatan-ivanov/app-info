package javax.appinfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.appinfo.test.IntegrationTestConfig;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.TEXT;
import static javax.appinfo.test.IntegrationTestConfig.APPINFO_PATH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jonatan Ivanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IntegrationTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AppInfoServletIntegrationTest {
    
    @Test
    public void statusCodeAndContentTypeTest() {
        when()
                .get(APPINFO_PATH)
        .then()
                .statusCode(200)
                .contentType(TEXT);
    }

    @Test
    public void contentTest() {
        String content = get(APPINFO_PATH).asString();

        assertPropertyExists("classLoading.loadedClassCount", content);
        assertPropertyExists("classLoading.totalLoadedClassCount", content);
        assertPropertyExists("classLoading.unloadedClassCount", content);
        assertPropertyExists("compilation.name", content);
        assertPropertyExists("compilation.totalCompilationTime", content);
    }

    @Test
    public void filteredContentTest() {
        String content = given()
                .queryParam("keys","classLoading")
                .get(APPINFO_PATH)
                .asString();

        assertPropertyExists("classLoading.loadedClassCount", content);
        assertPropertyExists("classLoading.totalLoadedClassCount", content);
        assertPropertyExists("classLoading.unloadedClassCount", content);

        assertPropertyDoesNotExist("compilation.name", content);
        assertPropertyDoesNotExist("compilation.totalCompilationTime", content);
    }

    @Test
    public void multiFilteredContentTest() {
        String content = given()
                .queryParam("keys","totalLoadedClassCount,totalCompilationTime")
                .get(APPINFO_PATH)
                .asString();

        assertPropertyExists("classLoading.totalLoadedClassCount", content);
        assertPropertyExists("compilation.totalCompilationTime", content);

        assertPropertyDoesNotExist("classLoading.loadedClassCount", content);
        assertPropertyDoesNotExist("classLoading.unloadedClassCount", content);
        assertPropertyDoesNotExist("compilation.name", content);
    }

    @Test
    public void epmtyFilterKeyTest() {
        String content = given()
                .queryParam("keys","")
                .get(APPINFO_PATH)
                .asString();

        assertPropertyExists("classLoading.loadedClassCount", content);
        assertPropertyExists("classLoading.totalLoadedClassCount", content);
        assertPropertyExists("classLoading.unloadedClassCount", content);
        assertPropertyExists("compilation.name", content);
        assertPropertyExists("compilation.totalCompilationTime", content);
    }

    private void assertPropertyExists(String key, String content) {
        assertThat(content).contains(key + ": ");
    }

    private void assertPropertyDoesNotExist(String key, String content) {
        assertThat(content).doesNotContain(key + ": ");
    }
}