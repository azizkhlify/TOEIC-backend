package com.toeic.backend.enrollments

import com.toeic.backend.classes.ClassesTable
import com.toeic.backend.users.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object EnrollmentsTable : Table("enrollments") {
    val id = varchar("id", 50)
    val classId = varchar("class_id", 50).references(ClassesTable.id)
    val studentId = varchar("student_id", 50).references(UsersTable.id)
    val joinedAt = timestamp("joined_at")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex("uq_enrollment", classId, studentId)
    }
}
