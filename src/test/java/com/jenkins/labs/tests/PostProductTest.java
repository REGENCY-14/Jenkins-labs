package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * PostProductTest - Test suite for the POST /products endpoint.
 *
 * <p>Validates that creating a new product returns the correct status code,
 * a generated ID, and echoes back the submitted fields. Also tests the API's
 * lenient behaviour when an empty body is submitted.</p>
 *
 * <p>Endpoint: {@code POST https://fakestoreapi.com/products}</p>
 *
 * <p><strong>Note:</strong> The Fake Store API is a mock — it does not persist
 * data. POST requests simulate creation and return a fake new ID.</p>
 */
public class PostProductTest extends BaseTest {

    /**
     * Builds a sample JSON product payload used across multiple test methods.
     *
     * @return a JSON string representing a new product
     */
    private String newProductJson() {
        return "{"
            + "\"title\": \"Test Product\","
            + "\"price\": 29.99,"
            + "\"description\": \"A test product for automation\","
            + "\"image\": \"https://fakestoreapi.com/img/placeholder.jpg\","
            + "\"category\": \"electronics\""
            + "}";
    }

    /**
     * Verifies that a valid POST request returns HTTP 201 Created.
     */
    @Test
    public void postProduct_shouldReturn201() {
        given()
            .contentType(ContentType.JSON)
            .body(newProductJson())
            .when()
                .post("/products")
            .then()
                .statusCode(201);
    }

    /**
     * Verifies that the response includes a newly generated {@code id} field.
     *
     * <p>The API assigns an ID to the created product and returns it in the response.</p>
     */
    @Test
    public void postProduct_shouldReturnNewId() {
        given()
            .contentType(ContentType.JSON)
            .body(newProductJson())
            .when()
                .post("/products")
            .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    /**
     * Verifies that the API echoes back the fields submitted in the request body.
     *
     * <p>Asserts that {@code title}, {@code price}, and {@code category} in the
     * response match the values that were sent in the request.</p>
     */
    @Test
    public void postProduct_shouldEchoBackSentFields() {
        // Extract the full response to assert individual field values
        Response response = given()
            .contentType(ContentType.JSON)
            .body(newProductJson())
            .when()
                .post("/products")
            .then()
                .statusCode(201)
                .extract().response();

        assertEquals("Test Product", response.jsonPath().getString("title"));
        assertEquals(29.99f, response.jsonPath().getFloat("price"), 0.01f);
        assertEquals("electronics", response.jsonPath().getString("category"));
    }

    /**
     * Verifies that the API handles an empty JSON body gracefully.
     *
     * <p>The Fake Store API is lenient and still returns HTTP 201 with a generated
     * ID even when no product fields are provided in the request body.</p>
     */
    @Test
    public void postProduct_emptyBody_shouldStillRespond() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")   // empty JSON object — no product fields provided
            .when()
                .post("/products")
            .then()
                .statusCode(201)
                .body("id", notNullValue());
    }
}
