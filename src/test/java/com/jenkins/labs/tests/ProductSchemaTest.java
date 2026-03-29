package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProductSchemaTest extends BaseTest {

    @Test
    public void getProductById_shouldMatchSchema() {
        given()
            .pathParam("id", 1)
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/product-schema.json"));
    }
}
