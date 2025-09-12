
package com.example.app

import io.restassured.RestAssured.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll
import org.hamcrest.Matchers.equalTo

class ApiE2ETest {

    companion object {
        var generalToken = ""
        @JvmStatic
        @BeforeAll
        fun setup() {
            baseURI = "http://localhost"
            port = 8080

            generalToken = given()
                .contentType("application/json")
                .body("""{"username":"admin","password":"secret"}""")
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .path<String>("token")
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
        .path<String>("token")
    
    // Paso 2: usar token para acceder a /ping
    given()
        .header("Authorization", "Bearer $token")
        .get("/ping")
        .then()
        .statusCode(200)
        .body(equalTo("pong"))

}

    @Test
    fun `echo endpoint should return received body`() {
        given()
            .header("Authorization", "Bearer $generalToken")
            .body("hello with token")
            .post("/echo")
            .then()
            .statusCode(200)
            .body(equalTo("Received: hello with token"))
    }

    @Test
    fun `login endpoint should fail with wrong credentials`() {
        given()
            .contentType("application/json")
            .body("""{"username":"admin2","password":"secret2"}""")
            .post("/login")
            .then()
            .statusCode(401)
    }
}
