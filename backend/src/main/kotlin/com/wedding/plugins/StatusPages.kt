package com.wedding.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import mu.KotlinLogging

/**
 * Configures status pages for error handling in the application.
 */
fun Application.configureStatusPages() {
    val logger = KotlinLogging.logger {}

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error(cause) { "Unhandled exception" }

            val status = when (cause) {
                is IllegalArgumentException -> HttpStatusCode.BadRequest
                is NoSuchElementException -> HttpStatusCode.NotFound
                is SecurityException -> HttpStatusCode.Forbidden
                else -> HttpStatusCode.InternalServerError
            }

            call.respond(
                status,
                ErrorResponse(
                    status = status.value,
                    message = cause.message ?: "An error occurred",
                ),
            )
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    message = "Resource not found",
                ),
            )
        }
    }
}

/**
 * Data class for standardized error responses.
 */
@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String,
)
