package com.toeic.backend

import at.favre.lib.crypto.bcrypt.BCrypt
import com.toeic.backend.auth.AuthService
import com.toeic.backend.auth.JwtService
import com.toeic.backend.classes.ClassRepository
import com.toeic.backend.classes.ClassService
import com.toeic.backend.classes.ClassesTable
import com.toeic.backend.enrollments.EnrollmentRepository
import com.toeic.backend.enrollments.EnrollmentsTable
import com.toeic.backend.plugins.*
import com.toeic.backend.users.UserRepository
import com.toeic.backend.users.UsersTable
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.atomic.AtomicInteger

private val dbCounter = AtomicInteger(0)

const val TEACHER_ID = "teacher-01"
const val STUDENT_ID = "student-01"
const val STUDENT2_ID = "student-02"
const val TEST_PASSWORD = "password123"

fun ApplicationTestBuilder.configureTestApp(): HttpClient {
    application {
        testModule()
    }
    return createClient {
        install(ContentNegotiation) { json() }
    }
}

fun Application.testModule() {
    configureSerialization()
    configureStatusPages()

    val jwtService = JwtService()
    configureAuthentication(jwtService)

    initTestDatabase()

    val userRepository = UserRepository()
    val classRepository = ClassRepository()
    val enrollmentRepository = EnrollmentRepository()

    val authService = AuthService(userRepository, jwtService)
    val classService = ClassService(classRepository, enrollmentRepository)

    configureRouting(authService, classService)
}

fun initTestDatabase() {
    val dbName = "test_${dbCounter.incrementAndGet()}"
    val db = Database.connect("jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", driver = "org.h2.Driver")
    com.toeic.backend.db.DatabaseFactory.database = db
    transaction(db) {
        SchemaUtils.create(UsersTable, ClassesTable, EnrollmentsTable)
        seedTestUsers()
    }
}

private fun seedTestUsers() {
    val hash = { pwd: String -> BCrypt.withDefaults().hashToString(12, pwd.toCharArray()) }

    UsersTable.insert {
        it[id] = TEACHER_ID
        it[fullName] = "Mondher Ben Ali"
        it[email] = "mondher@thee.tn"
        it[passwordHash] = hash(TEST_PASSWORD)
        it[role] = "teacher"
    }

    UsersTable.insert {
        it[id] = STUDENT_ID
        it[fullName] = "Yassine Kaibi"
        it[email] = "yassine@thee.tn"
        it[passwordHash] = hash(TEST_PASSWORD)
        it[role] = "student"
    }

    UsersTable.insert {
        it[id] = STUDENT2_ID
        it[fullName] = "Amira Trabelsi"
        it[email] = "amira@thee.tn"
        it[passwordHash] = hash(TEST_PASSWORD)
        it[role] = "student"
    }
}

suspend fun HttpClient.login(email: String, password: String = TEST_PASSWORD): String {
    val response = post("/api/v1/auth/login") {
        header(io.ktor.http.HttpHeaders.ContentType, io.ktor.http.ContentType.Application.Json.toString())
        setBody(mapOf("email" to email, "password" to password))
    }
    val body = response.body<kotlinx.serialization.json.JsonObject>()
    return body["accessToken"]!!.jsonPrimitive.content
}

fun HttpRequestBuilder.withAuth(token: String) {
    header("Authorization", "Bearer $token")
}
