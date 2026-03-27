package com.toeic.backend.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtService {
    private val secret = System.getenv("JWT_SECRET") ?: "dev-secret"
    private val issuer = System.getenv("JWT_ISSUER") ?: "toeic-backend"
    private val audience = System.getenv("JWT_AUDIENCE") ?: "toeic-backend"
    private val expirationMs = (System.getenv("JWT_EXPIRATION") ?: "3600000").toLong()

    val realm = "toeic-backend"

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
