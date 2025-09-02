
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
import io.ktor.server.auth.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondText("Error: ${cause.message}", status = io.ktor.http.HttpStatusCode.InternalServerError)
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
        }
        routing {
            authenticate("auth-basic") {
                get("/ping") {
                    call.respondText("pong")
                }
                post("/echo") {
                    val body = call.receive<String>()
                    call.respondText("Received: $body")
                }
                post("/login") {
                    call.respondText("Authenticated as ${call.principal<UserIdPrincipal>()?.name}")
                }
            }
        }
    }.start(wait = true)
}
