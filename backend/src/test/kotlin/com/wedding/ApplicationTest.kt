package com.wedding

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testHealthEndpoint() = testApplication {
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }

    @Test
    fun testGetPhotosEndpoint() = testApplication {
        // This is a basic test that just checks if the endpoint responds
        // In a real application, you'd want to mock the database and services
        val response = client.get("/photos")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    // More tests would be added here for each endpoint
    // For a complete test suite, you would:
    // 1. Mock the DatabaseFactory
    // 2. Mock the CloudinaryService
    // 3. Test each endpoint with various inputs
    // 4. Test error cases
}
