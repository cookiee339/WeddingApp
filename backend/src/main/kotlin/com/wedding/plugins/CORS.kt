package com.wedding.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the application.
 * 
 * Allows the frontend to make requests to the backend when they're 
 * hosted on different domains or ports.
 */
fun Application.configureCORS() {
    val config = ConfigFactory.load()
    val corsConfig = config.extract<CorsConfig>("cors")
    
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        
        // Allow all requested headers
        allowHeaders { true }
        
        // Process the cors configuration from application.conf
        corsConfig.hosts.forEach { host ->
            if (host == "*") {
                anyHost()
            } else {
                allowHost(host)
            }
        }
        
        // Max age for preflight requests (in seconds)
        maxAgeInSeconds = corsConfig.maxAge
    }
}

/**
 * Data class representing CORS configuration from application.conf
 */
data class CorsConfig(
    val hosts: List<String>,
    val methods: List<String>,
    val headers: List<String>,
    val maxAge: Long
)