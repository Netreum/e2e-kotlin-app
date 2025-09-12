
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

fun `authenticated flow with JWT token`() {

    // Paso 1: obtener token
    val token = given()
        .contentType("application/json")
        .body("""{"username":"admin","password":"secret"}""")
        .post("/login")
        .then()
        .statusCode(200)
        .extract()
        .path("token")
    
    // Paso 2: usar token para acceder a /ping
    given()
        .header("Authorization", "Bearer $token")
        .get("/ping")
        .then()
        .statusCode(200)
        .body(equalTo("pong"))

    // Paso 3: usar token para enviar datos a /echo
    given()
        .header("Authorization", "Bearer $token")
        .body("hello with token")
        .post("/echo")
        .then()
        .statusCode(200)
        .body(equalTo("Received: hello with token"))
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
    fun `login endpoint should fail with wrong credentials`() {
        given()
            .auth().preemptive().basic("admin", "wrong")
            .post("/login")
            .then()
            .statusCode(401)
    }
}
