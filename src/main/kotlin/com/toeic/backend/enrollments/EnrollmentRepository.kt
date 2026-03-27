package com.toeic.backend.enrollments

import com.toeic.backend.classes.ClassStudentItem
import com.toeic.backend.classes.ClassesTable
import com.toeic.backend.classes.StudentClassItem
import com.toeic.backend.classes.TeacherInfo
import com.toeic.backend.db.dbQuery
import com.toeic.backend.users.UsersTable
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class EnrollmentRepository {

    suspend fun insert(classId: String, studentId: String) = dbQuery {
        EnrollmentsTable.insert {
            it[EnrollmentsTable.id] = UUID.randomUUID().toString()
            it[EnrollmentsTable.classId] = classId
            it[EnrollmentsTable.studentId] = studentId
            it[EnrollmentsTable.joinedAt] = Clock.System.now()
        }
    }

    suspend fun exists(classId: String, studentId: String): Boolean = dbQuery {
        EnrollmentsTable.selectAll()
            .where { (EnrollmentsTable.classId eq classId) and (EnrollmentsTable.studentId eq studentId) }
            .count() > 0
    }

    suspend fun delete(classId: String, studentId: String) = dbQuery {
        EnrollmentsTable.deleteWhere {
            (EnrollmentsTable.classId eq classId) and (EnrollmentsTable.studentId eq studentId)
        }
    }

    suspend fun countByClassId(classId: String): Int = dbQuery {
        EnrollmentsTable.selectAll()
            .where { EnrollmentsTable.classId eq classId }
            .count().toInt()
    }

    suspend fun findStudentsByClassId(classId: String): List<ClassStudentItem> = dbQuery {
        (EnrollmentsTable innerJoin UsersTable)
            .selectAll()
            .where { EnrollmentsTable.classId eq classId }
            .map {
                ClassStudentItem(
                    id = it[UsersTable.id],
                    fullName = it[UsersTable.fullName],
                    email = it[UsersTable.email],
                    joinedAt = it[EnrollmentsTable.joinedAt].toString()
                )
            }
    }

    suspend fun findClassesByStudentId(studentId: String): List<StudentClassItem> = dbQuery {
        EnrollmentsTable
            .innerJoin(ClassesTable)
            .join(UsersTable, JoinType.INNER, ClassesTable.teacherId, UsersTable.id)
            .selectAll()
            .where { EnrollmentsTable.studentId eq studentId }
            .map {
                StudentClassItem(
                    id = it[ClassesTable.id],
                    name = it[ClassesTable.name],
                    teacher = TeacherInfo(fullName = it[UsersTable.fullName]),
                    joinedAt = it[EnrollmentsTable.joinedAt].toString()
                )
            }
    }
}
