package com.wedding.services

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.wedding.config.CloudinaryConfig
import mu.KotlinLogging
import java.io.InputStream

/**
 * Service for interacting with Cloudinary for image uploads.
 */
class CloudinaryService(config: CloudinaryConfig) : StorageService {
    private val logger = KotlinLogging.logger {}

    private val cloudinary = Cloudinary(
        ObjectUtils.asMap(
            "cloud_name",
            config.cloudName,
            "api_key",
            config.apiKey,
            "api_secret",
            config.apiSecret,
            "secure",
            true,
        ),
    )

    /**
     * Uploads an image to Cloudinary.
     *
     * @param imageStream The input stream of the image file
     * @param filename The name of the file (for reference only)
     * @return The URL of the uploaded image if successful, null otherwise
     */
    override suspend fun uploadImage(imageStream: InputStream, filename: String): String? {
        return try {
            // Convert the input stream to a byte array which Cloudinary can handle
            val bytes = imageStream.readAllBytes()

            // Upload options
            val options = ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "wedding_photos",
                "use_filename", true,
                "unique_filename", true,
                "quality", "auto:good",
                "fetch_format", "auto",
            )

            // Upload to Cloudinary using byte array instead of InputStream
            val result = cloudinary.uploader().upload(bytes, options)

            // Get secure URL
            val secureUrl = result["secure_url"] as String
            logger.info { "Successfully uploaded image $filename to Cloudinary at $secureUrl" }

            secureUrl
        } catch (e: Exception) {
            logger.error(e) { "Failed to upload image $filename to Cloudinary" }
            null
        } finally {
            try {
                imageStream.close()
            } catch (e: Exception) {
                logger.warn(e) { "Failed to close image stream" }
            }
        }
    }

    /**
     * Deletes an image from Cloudinary.
     *
     * @param identifier The identifier of the image to delete
     * @return True if deletion was successful, false otherwise
     */
    override suspend fun deleteImage(identifier: String): Boolean {
        return try {
            val result = cloudinary.uploader().destroy(identifier, ObjectUtils.emptyMap())
            val status = result["result"]

            val success = status == "ok"
            if (success) {
                logger.info { "Successfully deleted image $identifier from Cloudinary" }
            } else {
                logger.warn { "Failed to delete image $identifier from Cloudinary: $result" }
            }

            success
        } catch (e: Exception) {
            logger.error(e) { "Error deleting image $identifier from Cloudinary" }
            false
        }
    }

    /**
     * Extracts the public ID from a Cloudinary URL.
     *
     * @param url The Cloudinary URL
     * @return The public ID if found, null otherwise
     */
    fun extractPublicIdFromUrl(url: String): String? {
        // Example URL: https://res.cloudinary.com/cloud-name/image/upload/v1234567890/wedding_photos/abc123.jpg
        val regex = ".*/v\\d+/(.+?)\\.[^.]+$".toRegex()
        return regex.find(url)?.groupValues?.get(1)
    }

    override fun extractKeyFromUrl(url: String): String? = extractPublicIdFromUrl(url)
}
