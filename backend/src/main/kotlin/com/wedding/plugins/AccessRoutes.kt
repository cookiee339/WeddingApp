package com.wedding.plugins

import com.wedding.models.CreateAccessTokenRequest
import com.wedding.models.ValidateTokenRequest
import com.wedding.models.ValidateTokenResponse
import com.wedding.services.AccessControlService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAccessRoutes() {
    val accessControlService = AccessControlService()
    
    routing {
        route("/access") {
            
            // Validate access token
            post("/validate") {
                try {
                    val request = call.receive<ValidateTokenRequest>()
                    val isValid = accessControlService.validateToken(request.token)
                    call.respond(ValidateTokenResponse(valid = isValid))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ValidateTokenResponse(valid = false))
                }
            }
            
            // Generate new access token (for organizers)
            post("/generate") {
                try {
                    val request = call.receive<CreateAccessTokenRequest>()
                    val tokenResponse = accessControlService.generateToken(request)
                    call.respond(HttpStatusCode.Created, tokenResponse)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to generate token"))
                }
            }
            
            // Get all generated tokens (for organizers)
            get("/codes") {
                try {
                    val tokens = accessControlService.getAllTokens()
                    call.respond(tokens)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve tokens"))
                }
            }
            
            // Deactivate a token (for organizers)
            delete("/codes/{id}") {
                try {
                    val tokenId = call.parameters["id"]?.toIntOrNull()
                    if (tokenId == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid token ID"))
                        return@delete
                    }
                    
                    val success = accessControlService.deactivateToken(tokenId)
                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Token deactivated"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Token not found"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to deactivate token"))
                }
            }
            
            // Cleanup expired tokens (for organizers)
            post("/cleanup") {
                try {
                    val cleanedCount = accessControlService.cleanupExpiredTokens()
                    call.respond(mapOf("message" to "Cleaned up $cleanedCount expired tokens"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to cleanup tokens"))
                }
            }
        }
    }
}
