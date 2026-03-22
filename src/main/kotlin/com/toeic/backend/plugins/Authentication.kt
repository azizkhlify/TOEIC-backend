package com.toeic.backend.plugins

import com.toeic.backend.auth.JwtService
import com.toeic.backend.common.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureAuthentication(jwtService: JwtService) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtService.realm
            verifier(jwtService.verifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId")?.asString()
                val role = credential.payload.getClaim("role")?.asString()
                if (userId != null && role != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Token is expired or invalid", "INVALID_TOKEN")
                )
            }
        }
    }
}
