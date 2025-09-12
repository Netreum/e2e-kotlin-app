package com.example.app

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.statuspages.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

const val jwtIssuer = "ktor.io"
const val jwtAudience = "ktor-audience"
const val jwtRealm = "ktor sample app"
const val jwtSecret = "my-super-secret"

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            exception<Throwable> { call: ApplicationCall, cause: Throwable ->
                call.respondText("Error: ${cause.message}", status = HttpStatusCode.InternalServerError)
            }
        }
        install(Authentication) {
            basic("auth-basic") {
                realm = "Access to all endpoints"
                validate { credentials ->
                    if (credentials.name == "admin" && credentials.password == "secret") {
                        UserIdPrincipal(credentials.name)
                    } else null
                }
            }
            jwt("auth-jwt") {
                realm = jwtRealm
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(jwtSecret))
                        .withAudience(jwtAudience)
                        .withIssuer(jwtIssuer)
                        .build()
                )
                validate { credential ->
                    if (credential.payload.getClaim("username").asString() != "") {
                        JWTPrincipal(credential.payload)
                    } else null
                }
            }
        }
        routing {
            post("/login") {
                val credentials = call.receive<Map<String,String>>()
                val username = credentials["username"]
                val password = credentials["password"]
                if (username == "admin" && password == "secret") {
                    val token = generateToken(username)
                    call.respond(mapOf("token" to token))
                } else {
                    call.respondText("Invalid credentials", status = HttpStatusCode.Unauthorized)
                }
            }

            authenticate("auth-jwt") {
                get("/ping") {
                    call.respondText("pong")
                }
                post("/echo") {
                    val body = call.receive<String>()
                    call.respondText("Received: $body")
                }
            }
        }
    }.start(wait = true)
}
fun generateToken(username: String): String {
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim("username", username)
        .sign(Algorithm.HMAC256(jwtSecret))
}