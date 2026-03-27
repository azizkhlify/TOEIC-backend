package com.toeic.backend.plugins

import com.toeic.backend.auth.AuthService
import com.toeic.backend.auth.authRoutes
import com.toeic.backend.classes.ClassService
import com.toeic.backend.classes.classRoutes
import com.toeic.backend.students.studentRoutes
import com.toeic.backend.teachers.teacherRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(authService: AuthService, classService: ClassService) {
    routing {
        route("/api/v1") {
            get("/health") {
                call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
            }

            authRoutes(authService)

            authenticate("auth-jwt") {
                classRoutes(classService)
                teacherRoutes(classService)
                studentRoutes(classService)
            }
        }
    }
}
