package com.toeic.backend.common

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun RoutingCall.userId(): String =
    principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
        ?: throw UnauthorizedException("Missing user ID in token")

fun RoutingCall.userRole(): String =
    principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
        ?: throw UnauthorizedException("Missing role in token")

fun RoutingCall.requireRole(role: String) {
    if (userRole() != role) {
        throw ForbiddenException(
            message = "Role '$role' required",
            errorCode = "${role.uppercase()}_ROLE_REQUIRED"
        )
    }
}
