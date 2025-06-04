package com.wedding.plugins

import com.wedding.services.StorageService
import com.wedding.services.PhotoService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.util.UUID
import kotlin.math.ceil

/** Configures routing for the application. */
fun Application.configureRouting(storageService: StorageService, photoService: PhotoService) {
    val logger = KotlinLogging.logger {}

    // Constants for pagination defaults
    val DEFAULT_PAGE = 1
    val DEFAULT_LIMIT = 20
    val MAX_LIMIT = 100

    // Standard error response format
    @Serializable
    data class ErrorResponse(val error: String, val details: String? = null)

    // Data classes for request/response
    data class DeletePhotoRequest(val uploader_id: String)

    @Serializable
    data class PaginatedResponse<T>(
        val data: List<T>,
        val page: Int,
        val limit: Int,
        val total: Int,
        val pages: Int,
    )

    routing {
        // Health check endpoint
        get("/health") { call.respondText("OK") }

        // Photos API
        route("/photos") {
            // Get all photos with optional filtering
            get {
                val uploaderId = call.request.queryParameters["uploaderId"]
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: DEFAULT_PAGE
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: DEFAULT_LIMIT

                // Validate pagination parameters
                if (page <= 0 || limit <= 0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid pagination parameters", "Page and limit must be positive integers"),
                    )
                    return@get
                }

                // Apply maximum limit to prevent excessive data retrieval
                val effectiveLimit = if (limit > MAX_LIMIT) MAX_LIMIT else limit

                // Get photos with count for pagination
                val (photos, totalCount) = photoService.getAllPhotosWithCount(uploaderId, page, effectiveLimit)
                val totalPages = ceil(totalCount.toDouble() / effectiveLimit).toInt()

                // Return paginated response
                call.respond(
                    PaginatedResponse(
                        data = photos,
                        page = page,
                        limit = effectiveLimit,
                        total = totalCount,
                        pages = totalPages,
                    ),
                )
            }

            // Get a specific photo by ID
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid request", "Photo ID must be a valid integer"),
                    )
                    return@get
                }

                val photo = photoService.getPhotoById(id)
                if (photo != null) {
                    call.respond(photo)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("Resource not found", "Photo with ID $id does not exist"),
                    )
                }
            }

            // Upload a new photo
            post {
                // Handle multipart data upload
                val multipart = call.receiveMultipart()
                var uploaderId: String? = null
                var imageStream: PartData.FileItem? = null
                var fileName: String? = null

                try {
                    // Process each part of the multipart request
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                if (part.name == "uploader_id") {
                                    uploaderId = part.value
                                }
                                // Always dispose form items after processing
                                part.dispose.invoke()
                            }
                            is PartData.FileItem -> {
                                // If we already have an image, dispose the new one
                                if (imageStream != null) {
                                    part.dispose.invoke()
                                } else {
                                    imageStream = part
                                    fileName = part.originalFileName ?: "unknown_${UUID.randomUUID()}.jpg"
                                }
                            }
                            else -> part.dispose.invoke()
                        }
                    }

                    // Validate required fields
                    if (uploaderId.isNullOrBlank()) {
                        imageStream?.dispose?.invoke()
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Missing required field", "uploader_id is required"),
                        )
                        return@post
                    }

                    if (imageStream == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Missing required field", "Image file is required"),
                        )
                        return@post
                    }

                    // Upload to storage backend
                    val imageUrl = storageService.uploadImage(
                        imageStream!!.streamProvider(),
                        fileName!!,
                    )

                    // Always dispose the image stream after using it
                    imageStream!!.dispose.invoke()

                    if (imageUrl == null) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse("Upload failed", "Failed to upload image to cloud storage"),
                        )
                        return@post
                    }

                    // Save to database
                    val photo = photoService.createPhoto(imageUrl, uploaderId!!)
                    if (photo != null) {
                        call.respond(HttpStatusCode.Created, photo)
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse("Database error", "Failed to save photo metadata"),
                        )
                    }
                } catch (e: Exception) {
                    // Ensure resources are cleaned up in case of exception
                    imageStream?.dispose?.invoke()

                    logger.error(e) { "Error processing photo upload" }
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Server error", "Error processing upload: ${e.message}"),
                    )
                }
            }

            // Delete a photo
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid request", "Photo ID must be a valid integer"),
                    )
                    return@delete
                }

                // Get uploader ID from request body for verification
                val request = try {
                    call.receive<Map<String, String>>()
                } catch (e: Exception) {
                    logger.warn { "Failed to parse delete request body: ${e.message}" }
                    null
                }
                val uploaderId = request?.get("uploader_id")
                
                logger.info { "Delete request for photo $id: uploaderId from request = '$uploaderId'" }

                // Get the photo first to get the Cloudinary URL
                val photo = photoService.getPhotoById(id)
                if (photo == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("Resource not found", "Photo with ID $id does not exist"),
                    )
                    return@delete
                }

                // Verify uploader ID if provided
                if (uploaderId != null && photo.uploaderId != uploaderId) {
                    logger.warn { "Upload ID mismatch: request='$uploaderId', photo='${photo.uploaderId}'" }
                    call.respond(
                        HttpStatusCode.Forbidden,
                        ErrorResponse("Access denied", "You don't have permission to delete this photo"),
                    )
                    return@delete
                } else {
                    logger.info { "Upload ID verification passed or skipped: request='$uploaderId', photo='${photo.uploaderId}'" }
                }

                // Delete from database
                val deleted = photoService.deletePhoto(id, uploaderId)
                if (!deleted) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Database error", "Failed to delete photo from database"),
                    )
                    return@delete
                }

                // Try to delete from storage as well, but don't fail the request if this fails
                val key = storageService.extractKeyFromUrl(photo.imageUrl)
                if (key != null) {
                    val deletedFromStorage = storageService.deleteImage(key)
                    if (!deletedFromStorage) {
                        logger.warn { "Failed to delete image from storage: $key" }
                    }
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
