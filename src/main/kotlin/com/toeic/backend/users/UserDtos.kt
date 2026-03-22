package com.toeic.backend.users

data class UserRow(
    val id: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val role: String
)
