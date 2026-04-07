package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * GetProductByIdTest - Test suite for the GET /products/{id} endpoint.
 *
 * <p>Validates status codes, response body fields, data types, and edge cases
 * such as requesting a product with a non-existent ID.</p>
 *
 * <p>Endpoint: {@code GET https://fakestoreapi.com/products/{id}}</p>
 */
public class GetProductByIdTest extends BaseTest {

    /** A known valid product ID that exists in the Fake Store API. */
    private static final int VALID_PRODUCT_ID = 1;

    /** A product ID that does not exist, used to test edge case behaviour. */
    private static final int INVALID_PRODUCT_ID = 9999;

    /**
     * Verifies that requesting a valid product ID returns HTTP 200 OK.
     */
    @Test
    public void getProductById_shouldReturn200() {
        given()
            .pathParam("id", VALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200);
    }

    /**
     * Verifies that the returned product's {@code id} field matches the requested ID.
     *
     * <p>Ensures the API returns the correct product and not a generic response.</p>
     */
    @Test
    public void getProductById_shouldReturnCorrectId() {
        given()
            .pathParam("id", VALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200)
                .body("id", equalTo(VALID_PRODUCT_ID));
    }

    /**
     * Verifies that a valid product response contains all expected fields.
     *
     * <p>Checks for: {@code id}, {@code title}, {@code price}, {@code description},
     * {@code category}, {@code image}, {@code rating.rate}, and {@code rating.count}.</p>
     */
    @Test
    public void getProductById_shouldHaveAllFields() {
        given()
            .pathParam("id", VALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200)
                .body("id",           notNullValue())
                .body("title",        notNullValue())
                .body("price",        notNullValue())
                .body("description",  notNullValue())
                .body("category",     notNullValue())
                .body("image",        notNullValue())
                .body("rating",       notNullValue())
                .body("rating.rate",  notNullValue())
                .body("rating.count", notNullValue());
    }

    /**
     * Verifies that the product price is a positive number.
     *
     * <p>Extracts the {@code price} field and asserts it is greater than zero,
     * ensuring no free or negatively priced products are returned.</p>
     */
    @Test
    public void getProductById_priceShouldBePositive() {
        // Extract the full response to perform custom assertions on the price field
        Response response = given()
            .pathParam("id", VALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200)
                .extract().response();

        float price = response.jsonPath().getFloat("price");
        assertTrue("Price should be greater than 0", price > 0);
    }

    /**
     * Verifies the API behaviour when requesting a non-existent product ID.
     *
     * <p>The Fake Store API returns HTTP 200 with an empty or null body for unknown IDs
     * rather than a 404. This test documents and validates that known behaviour.</p>
     */
    @Test
    public void getProductById_invalidId_shouldReturnEmptyBody() {
        Response response = given()
            .pathParam("id", INVALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .extract().response();

        // API returns 200 with empty or "null" body for non-existent products
        assertEquals(200, response.statusCode());
        assertTrue("Body should be empty or null",
            response.getBody().asString().isEmpty() ||
            response.getBody().asString().equals("null"));
    }
}
