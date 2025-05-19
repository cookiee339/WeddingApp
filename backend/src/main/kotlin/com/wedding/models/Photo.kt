package com.wedding.models

import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Represents a photo in the application.
 *
 * @property photoId The unique identifier for this photo
 * @property imageUrl The URL to the image stored in Cloudinary
 * @property uploaderId The identifier for the user who uploaded this photo
 * @property uploadedAt The timestamp when the photo was uploaded
 */
@Serializable
data class Photo(
    val photoId: Int,
    val imageUrl: String,
    val uploaderId: String,
    val uploadedAt: String // ISO 8601 timestamp
)

/**
 * Data class for photo creation request.
 */
@Serializable
data class PhotoUploadRequest(
    val uploaderId: String
)

/**
 * Response class for a successful photo upload.
 */
@Serializable
data class PhotoUploadResponse(
    val photoId: Int,
    val imageUrl: String,
    val uploaderId: String,
    val uploadedAt: String
)