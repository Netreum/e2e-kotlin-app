
package com.example.app

import io.restassured.RestAssured.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll
import org.hamcrest.Matchers.equalTo

class ApiE2ETest : BaseTest(){

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
    fun `Acceso a endpoint protegido con token valido`() {

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
    fun `Envio de datos a endpoint protegido`() {
        given()
            .header("Authorization", "Bearer $generalToken")
            .body("hello with token")
            .post("/echo")
            .then()
            .statusCode(200)
            .body(equalTo("Received: hello with token"))
    }

    @Test
    fun `Login con credenciales invalidas`() {
        given()
            .contentType("application/json")
            .body("""{"username":"admin2","password":"secret2"}""")
            .post("/login")
            .then()
            .statusCode(401)
            .body(equalTo("Invalid credentials"))
    }

    @Test
    fun `Envio de datos con body vacio`() {
        given()
            .header("Authorization", "Bearer $generalToken")
            .post("/echo")
            .then()
            .statusCode(200)
            .body(equalTo("Received: "))
    }

    @Test
    fun `Acceso a endpoint protegido sin token`() {
        given()
            .get("/ping")
            .then()
            .statusCode(401)
    }

}
