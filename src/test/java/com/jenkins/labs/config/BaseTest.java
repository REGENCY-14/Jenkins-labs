package com.jenkins.labs.config;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

/**
 * BaseTest - Base configuration class for all API tests.
 *
 * <p>All test classes extend this class to inherit the shared REST Assured
 * configuration. This ensures the base URI is set once before any test runs,
 * avoiding duplication across test classes.</p>
 *
 * <p>API under test: <a href="https://fakestoreapi.com">Fake Store API</a></p>
 */
public class BaseTest {

    /**
     * Configures the base URI for REST Assured before any test in the suite runs.
     *
     * <p>Annotated with {@code @BeforeClass} so it executes once per test class,
     * not before every individual test method.</p>
     */
    @BeforeClass
    public static void setup() {
        // Set the base URL for all REST Assured requests in every test class
        RestAssured.baseURI = "https://fakestoreapi.com";
    }
}
