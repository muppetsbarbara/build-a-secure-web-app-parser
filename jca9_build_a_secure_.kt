package com_secure_web_app_parser

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import com.google.gson.GsonBuilder

fun main() {
    val gson = GsonBuilder().create()

    embeddedServer(Netty, port = 8080) {
        install(Routing)
        install(HttpsRedirect)
        install(CallLogging)
        install(DefaultHeaders)
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(Authentication) {
            jwt("jwt") {
                realm = "jca9_build_a_secure_"
                verifier(
                    jwtVerifier(
                        "my_secret_key",
                        "my_issuer",
                        algorithm = Algorithm.HMAC256("my_secret_key")
                    )
                )
                validate {
                    it.contains("username")
                }
            }
        }
        
        routing {
            get("/") {
                call.respond("Welcome to jca9 build a secure web app parser!")
            }
            post("/parse") {
                val request = call.receive<TextPlain>()
                val parser = MyParser()
                val result = parser.parse(request.text)
                call.respond(result)
            }
        }
    }.start(wait = true)
}

data class TextPlain(val text: String)

class MyParser {
    fun parse(input: String): String {
        // implement your parsing logic here
        return "Parsed result: $input"
    }
}