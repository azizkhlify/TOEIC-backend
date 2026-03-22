package com.toeic.backend.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import java.util.*

class JwtService(environment: ApplicationEnvironment) {
    private val secret = environment.config.property("jwt.secret").getString()
    private val issuer = environment.config.property("jwt.issuer").getString()
    private val audience = environment.config.property("jwt.audience").getString()
    private val expirationMs = environment.config.property("jwt.expiration").getString().toLong()

    val realm = environment.config.property("jwt.realm").getString()

    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun generateToken(userId: String, role: String): String = JWT.create()
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withClaim("role", role)
        .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
        .sign(Algorithm.HMAC256(secret))
}
