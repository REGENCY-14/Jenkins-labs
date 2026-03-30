package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class GetProductByIdTest extends BaseTest {

    private static final int VALID_PRODUCT_ID = 1;
    private static final int INVALID_PRODUCT_ID = 9999;

    @Test
    public void getProductById_shouldReturn200() {
        given()
            .pathParam("id", VALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200);
    }

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

    @Test
    public void getProductById_shouldHaveAllFields() {
        given()
            .pathParam("id", VALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", notNullValue())
                .body("price", notNullValue())
                .body("description", notNullValue())
                .body("category", notNullValue())
                .body("image", notNullValue())
                .body("rating", notNullValue())
                .body("rating.rate", notNullValue())
                .body("rating.count", notNullValue());
    }

    @Test
    public void getProductById_priceShouldBePositive() {
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

    @Test
    public void getProductById_invalidId_shouldReturnEmptyBody() {
        // FakeStoreAPI returns 200 with empty body for non-existent products
        Response response = given()
            .pathParam("id", INVALID_PRODUCT_ID)
            .when()
                .get("/products/{id}")
            .then()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertTrue("Body should be empty or null",
            response.getBody().asString().isEmpty() ||
            response.getBody().asString().equals("null"));
    }
}
