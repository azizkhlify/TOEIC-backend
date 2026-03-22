package com.toeic.backend.students

import com.toeic.backend.classes.ClassService
import com.toeic.backend.common.requireRole
import com.toeic.backend.common.respondList
import com.toeic.backend.common.userId
import io.ktor.server.routing.*

fun Route.studentRoutes(classService: ClassService) {
    route("/students/me") {
        get("/classes") {
            call.requireRole("student")
            val classes = classService.getStudentClasses(call.userId())
            call.respondList(classes)
        }
    }
}
