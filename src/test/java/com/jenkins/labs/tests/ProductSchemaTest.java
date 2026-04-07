package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * ProductSchemaTest - JSON schema validation tests for the product response.
 *
 * <p>Uses REST Assured's built-in JSON Schema Validator to assert that the
 * structure of the API response matches the expected schema definition.</p>
 *
 * <p>The schema file is located at:
 * {@code src/test/resources/schemas/product-schema.json}</p>
 *
 * <p>Endpoint: {@code GET https://fakestoreapi.com/products/{id}}</p>
 */
public class ProductSchemaTest extends BaseTest {

    /**
     * Verifies that a single product response matches the defined JSON schema.
     *
     * <p>The schema enforces that required fields ({@code id}, {@code title},
     * {@code price}, {@code description}, {@code category}, {@code image},
     * {@code rating}) are present and have the correct data types.</p>
     */
    @Test
    public void getProductById_shouldMatchSchema() {
        given()
            .pathParam("id", 1)   // use product ID 1 as a known valid product
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200)
                // validate the response body against the JSON schema file on the classpath
                .body(matchesJsonSchemaInClasspath("schemas/product-schema.json"));
    }
}
