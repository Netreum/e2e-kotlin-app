
package com.example.app

import io.restassured.RestAssured.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll
import org.hamcrest.Matchers.equalTo

class ApiE2ETest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            baseURI = "http://localhost"
            port = 8080
        }
    }

    @Test
    fun `ping endpoint should return pong`() {
        get("/ping")
            .then()
            .statusCode(200)
            .body(equalTo("pong"))
    }

    @Test
    fun `echo endpoint should return received body`() {
        given()
            .body("hello")
            .post("/echo")
            .then()
            .statusCode(200)
            .body(equalTo("Received: hello"))
    }

    @Test
    fun `login endpoint should authenticate`() {
        given()
            .auth().preemptive().basic("admin", "secret")
            .post("/login")
            .then()
            .statusCode(200)
            .body(equalTo("Authenticated as admin"))
    }

    @Test
    fun `login endpoint should fail with wrong credentials`() {
        given()
            .auth().preemptive().basic("admin", "wrong")
            .post("/login")
            .then()
            .statusCode(401)
    }
}
