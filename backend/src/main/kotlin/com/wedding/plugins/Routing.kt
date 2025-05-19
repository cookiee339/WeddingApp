package com.wedding.plugins

import com.wedding.models.Photo
import com.wedding.services.CloudinaryService
import com.wedding.services.PhotoService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.util.*

/**
 * Configures routing for the application.
 */
fun Application.configureRouting(cloudinaryService: CloudinaryService, photoService: PhotoService) {
    val logger = KotlinLogging.logger {}
    
    // Constants for pagination defaults
    val DEFAULT_PAGE = 1
    val DEFAULT_LIMIT = 20
    val MAX_LIMIT = 100
    
    // Standard error response format
    data class ErrorResponse(val error: String, val details: String? = null)
    
    // Data classes for request/response
    data class DeletePhotoRequest(val uploader_id: String)
    
    data class PaginatedResponse<T>(
        val data: List<T>,
        val page: Int,
        val limit: Int,
        val total: Int,
        val pages: Int
    )
    
    routing {
        // Health check endpoint
        get("/health") {
            call.respondText("OK")
        }
        
        // Photos API
        route("/photos") {
            // Get all photos with optional filtering
            get {
                val uploaderId = call.request.queryParameters["uploaderId"]
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                
                if (page <= 0 || limit <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "Page and limit must be positive integers")
                    return@get
                }
                
                val photos = photoService.getAllPhotos(uploaderId, page, limit)
                call.respond(photos)
            }
            
            // Get a specific photo by ID
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid photo ID")
                    return@get
                }
                
                val photo = photoService.getPhotoById(id)
                if (photo != null) {
                    call.respond(photo)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Photo not found")
                }
            }
            
            // Upload a new photo
            post {
                // Handle multipart data upload
                val multipart = call.receiveMultipart()
                var uploaderId: String? = null
                var imageStream: PartData.FileItem? = null
                var fileName: String? = null
                
                // Process each part of the multipart request
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "uploader_id") {
                                uploaderId = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            imageStream = part
                            fileName = part.originalFileName ?: "unknown_${UUID.randomUUID()}.jpg"
                        }
                        else -> {}
                    }
                    // Don't dispose parts yet as we need the file stream
                }
                
                // Validate required fields
                if (uploaderId == null || imageStream == null) {
                    imageStream?.dispose?.let { it1 -> it1() }
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Missing required fields: uploader_id and image file"
                    )
                    return@post
                }
                
                try {
                    // Upload to Cloudinary
                    val cloudinaryUrl = cloudinaryService.uploadImage(imageStream!!.streamProvider(), fileName!!)
                    imageStream!!.dispose()
                    
                    if (cloudinaryUrl == null) {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to upload image to cloud storage")
                        return@post
                    }
                    
                    // Save to database
                    val photo = photoService.createPhoto(cloudinaryUrl, uploaderId!!)
                    if (photo != null) {
                        call.respond(HttpStatusCode.Created, photo)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to save photo metadata")
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Error processing photo upload" }
                    call.respond(HttpStatusCode.InternalServerError, "Error processing upload: ${e.message}")
                }
            }
            
            // Delete a photo
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid photo ID")
                    return@delete
                }
                
                // Get uploader ID from request body for verification
                val request = call.receiveOrNull<Map<String, String>>()
                val uploaderId = request?.get("uploader_id")
                
                // Get the photo first to get the Cloudinary URL
                val photo = photoService.getPhotoById(id)
                if (photo == null) {
                    call.respond(HttpStatusCode.NotFound, "Photo not found")
                    return@delete
                }
                
                // Verify uploader ID if provided
                if (uploaderId != null && photo.uploaderId != uploaderId) {
                    call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this photo")
                    return@delete
                }
                
                // Delete from database
                val deleted = photoService.deletePhoto(id, uploaderId)
                if (!deleted) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete photo from database")
                    return@delete
                }
                
                // Try to delete from Cloudinary as well, but don't fail the request if this fails
                val publicId = cloudinaryService.extractPublicIdFromUrl(photo.imageUrl)
                if (publicId != null) {
                    val cloudinaryDeleted = cloudinaryService.deleteImage(publicId)
                    if (!cloudinaryDeleted) {
                        logger.warn { "Failed to delete image from Cloudinary: $publicId" }
                    }
                }
                
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}