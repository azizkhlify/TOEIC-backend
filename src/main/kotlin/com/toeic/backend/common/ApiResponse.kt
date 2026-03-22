package com.toeic.backend.common

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String, val errorCode: String)

@Serializable
data class ListResponse<T>(val items: List<T>)

suspend inline fun <reified T> RoutingCall.respondList(items: List<T>) {
    if (items.isEmpty()) {
        respond(HttpStatusCode.NoContent)
    } else {
        respond(HttpStatusCode.OK, ListResponse(items))
    }
}
