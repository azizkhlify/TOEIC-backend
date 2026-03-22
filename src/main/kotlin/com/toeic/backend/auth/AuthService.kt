package com.toeic.backend.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.toeic.backend.common.UnauthorizedException
import com.toeic.backend.users.UserRepository

class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    suspend fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw UnauthorizedException()

        val passwordMatch = BCrypt.verifyer()
            .verify(request.password.toCharArray(), user.passwordHash)
            .verified

        if (!passwordMatch) {
            throw UnauthorizedException()
        }

        val token = jwtService.generateToken(user.id, user.role)

        return LoginResponse(
            accessToken = token,
            user = UserInfo(
                id = user.id,
                fullName = user.fullName,
                email = user.email,
                role = user.role
            )
        )
    }
}
