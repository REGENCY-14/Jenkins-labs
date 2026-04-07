package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * GetProductsTest - Test suite for the GET /products endpoint.
 *
 * <p>Validates that the products list endpoint returns the correct HTTP status,
 * a non-empty response body, required fields on each product, and respects
 * the optional {@code limit} query parameter.</p>
 *
 * <p>Endpoint: {@code GET https://fakestoreapi.com/products}</p>
 */
public class GetProductsTest extends BaseTest {

    /**
     * Verifies that GET /products returns HTTP 200 OK.
     *
     * <p>This is the most basic health check for the endpoint.</p>
     */
    @Test
    public void getAllProducts_shouldReturn200() {
        given()
            .when()
                .get("/products")
            .then()
                .statusCode(200);
    }

    /**
     * Verifies that GET /products returns a non-empty list of products.
     *
     * <p>Asserts that the response body is a JSON array with at least one element.</p>
     */
    @Test
    public void getAllProducts_shouldReturnNonEmptyList() {
        given()
            .when()
                .get("/products")
            .then()
                .statusCode(200)
                .body("$", not(empty()))           // root array is not empty
                .body("size()", greaterThan(0));    // at least one product exists
    }

    /**
     * Verifies that the first product in the list contains all required fields.
     *
     * <p>Checks that {@code id}, {@code title}, {@code price}, {@code category},
     * and {@code image} are all present and non-null on the first item.</p>
     */
    @Test
    public void getAllProducts_eachProductHasRequiredFields() {
        given()
            .when()
                .get("/products")
            .then()
                .statusCode(200)
                .body("[0].id",       notNullValue())
                .body("[0].title",    notNullValue())
                .body("[0].price",    notNullValue())
                .body("[0].category", notNullValue())
                .body("[0].image",    notNullValue());
    }

    /**
     * Verifies that the {@code limit} query parameter restricts the number of results.
     *
     * <p>Sends a request with {@code ?limit=5} and asserts that exactly 5 products
     * are returned in the response array.</p>
     */
    @Test
    public void getAllProducts_withLimit_shouldReturnCorrectCount() {
        int limit = 5;

        // Send request with limit query param and extract the full response
        Response response = given()
            .queryParam("limit", limit)
            .when()
                .get("/products")
            .then()
                .statusCode(200)
                .extract().response();

        // Assert the returned list size matches the requested limit
        int actualSize = response.jsonPath().getList("$").size();
        assertEquals("Expected " + limit + " products", limit, actualSize);
    }
}
