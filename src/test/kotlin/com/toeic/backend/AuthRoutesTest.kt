package com.toeic.backend

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthRoutesTest {

    @Test
    fun `login with valid credentials returns token and user info`() = testApplication {
        val client = configureTestApp()

        val response = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to "mondher@thee.tn", "password" to TEST_PASSWORD))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<JsonObject>()
        assertNotNull(body["accessToken"])
        val user = body["user"] as JsonObject
        assertEquals("teacher", user["role"]?.jsonPrimitive?.content)
        assertEquals("Mondher Ben Ali", user["fullName"]?.jsonPrimitive?.content)
    }

    @Test
    fun `login with wrong password returns 401`() = testApplication {
        val client = configureTestApp()

        val response = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to "mondher@thee.tn", "password" to "wrongpass"))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val body = response.body<JsonObject>()
        assertEquals("INVALID_CREDENTIALS", body["errorCode"]?.jsonPrimitive?.content)
    }

    @Test
    fun `login with unknown email returns 401`() = testApplication {
        val client = configureTestApp()

        val response = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to "nobody@thee.tn", "password" to TEST_PASSWORD))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `accessing protected route without token returns 401`() = testApplication {
        val client = configureTestApp()

        val response = client.get("/api/v1/teachers/me/classes")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
