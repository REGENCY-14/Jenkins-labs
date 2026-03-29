package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class GetProductsTest extends BaseTest {

    @Test
    public void getAllProducts_shouldReturn200() {
        given()
            .when()
                .get("/products")
            .then()
                .statusCode(200);
    }

    @Test
    public void getAllProducts_shouldReturnNonEmptyList() {
        given()
            .when()
                .get("/products")
            .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("size()", greaterThan(0));
    }

    @Test
    public void getAllProducts_eachProductHasRequiredFields() {
        given()
            .when()
                .get("/products")
            .then()
                .statusCode(200)
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].price", notNullValue())
                .body("[0].category", notNullValue())
                .body("[0].image", notNullValue());
    }

    @Test
    public void getAllProducts_withLimit_shouldReturnCorrectCount() {
        int limit = 5;
        Response response = given()
            .queryParam("limit", limit)
            .when()
                .get("/products")
            .then()
                .statusCode(200)
                .extract().response();

        int actualSize = response.jsonPath().getList("$").size();
        assertEquals("Expected " + limit + " products", limit, actualSize);
    }
}
