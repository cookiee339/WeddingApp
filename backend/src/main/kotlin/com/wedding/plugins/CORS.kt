package com.wedding.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the application.
 * * Allows the frontend to make requests to the backend when they're * hosted on different domains or ports.
 */
fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)

        // Allow all hosts for development
        anyHost()

        // Allow all requested headers
        allowHeaders { true }
        
        // Allow common headers explicitly
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("X-Requested-With")

        // Allow credentials if needed
        allowCredentials = false

        // Max age for preflight requests (in seconds)
        maxAgeInSeconds = 3600
        
        // Ensure CORS headers are added to all responses, including error responses
        allowNonSimpleContentTypes = true
    }
}

/**
 * Data class representing CORS configuration from application.conf
 */
data class CorsConfig(
    val hosts: List<String>,
    val methods: List<String>,
    val headers: List<String>,
    val maxAge: Long,
)
