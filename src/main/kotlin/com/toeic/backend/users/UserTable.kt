package com.toeic.backend.users

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = varchar("id", 50)
    val fullName = varchar("full_name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20) // "teacher" or "student"

    override val primaryKey = PrimaryKey(id)
}
