package com.toeic.backend.classes

import com.toeic.backend.users.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ClassesTable : Table("classes") {
    val id = varchar("id", 50)
    val name = varchar("name", 255)
    val description = varchar("description", 1000).nullable()
    val joinCode = varchar("join_code", 20).uniqueIndex()
    val teacherId = varchar("teacher_id", 50).references(UsersTable.id)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
