package com.toeic.backend.classes

import com.toeic.backend.common.requireRole
import com.toeic.backend.common.respondList
import com.toeic.backend.common.userId
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.classRoutes(classService: ClassService) {
    route("/classes") {
        post {
            call.requireRole("teacher")
            val request = call.receive<CreateClassRequest>()
            val response = classService.createClass(call.userId(), request)
            call.respond(HttpStatusCode.Created, response)
        }

        post("/join") {
            call.requireRole("student")
            val request = call.receive<JoinClassRequest>()
            classService.joinClass(call.userId(), request)
            call.respond(HttpStatusCode.OK)
        }

        get("/{classId}/students") {
            call.requireRole("teacher")
            val classId = call.parameters["classId"]!!
            val students = classService.getClassStudents(call.userId(), classId)
            call.respondList(students)
        }

        delete("/{classId}/students/{studentId}") {
            call.requireRole("teacher")
            val classId = call.parameters["classId"]!!
            val studentId = call.parameters["studentId"]!!
            classService.removeStudent(call.userId(), classId, studentId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
