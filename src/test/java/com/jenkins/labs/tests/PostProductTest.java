package com.jenkins.labs.tests;

import com.jenkins.labs.config.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PostProductTest extends BaseTest {

    // Sample product payload
    private String newProductJson() {
        return "{"
            + "\"title\": \"Test Product\","
            + "\"price\": 29.99,"
            + "\"description\": \"A test product for automation\","
            + "\"image\": \"https://fakestoreapi.com/img/placeholder.jpg\","
            + "\"category\": \"electronics\""
            + "}";
    }

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

    @Test
    public void postProduct_shouldEchoBackSentFields() {
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

    @Test
    public void postProduct_emptyBody_shouldStillRespond() {
        // FakeStoreAPI is lenient — it still returns 201 with an id
        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
                .post("/products")
            .then()
                .statusCode(201)
                .body("id", notNullValue());
    }
}
