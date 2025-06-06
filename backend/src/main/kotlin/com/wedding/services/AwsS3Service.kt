package com.wedding.services

import com.wedding.config.AwsS3Config
import mu.KotlinLogging
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream

/**
 * Service for interacting with AWS S3 for image uploads.
 */
class AwsS3Service(config: AwsS3Config) : StorageService {
    private val logger = KotlinLogging.logger {}
    private val bucket = config.bucket
    private val region = config.region
    private val s3: S3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(config.accessKeyId, config.secretAccessKey),
            ),
        )
        .build()

    /**
     * Uploads an image to AWS S3.
     *
     * @param imageStream The input stream of the image file
     * @param filename The name of the file (for reference only)
     * @return The URL of the uploaded image if successful, null otherwise
     */
    override suspend fun uploadImage(imageStream: InputStream, filename: String): String? {
        return try {
            val key = "wedding_photos/$filename"
            val putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .build()

            s3.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(imageStream, imageStream.available().toLong()))

            val url = generatePresignedUrl(key)
            logger.info { "Successfully uploaded image $filename to S3 at $url" }
            url
        } catch (e: Exception) {
            logger.error(e) { "Failed to upload image $filename to S3" }
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
     * Deletes an image from AWS S3.
     *
     * @param identifier The S3 key of the image to delete (e.g., wedding_photos/filename.jpg)
     * @return True if deletion was successful, false otherwise
     */
    override suspend fun deleteImage(identifier: String): Boolean {
        return try {
            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(identifier)
                .build()
            s3.deleteObject(deleteRequest)
            logger.info { "Successfully deleted image $identifier from S3" }
            true
        } catch (e: Exception) {
            logger.error(e) { "Error deleting image $identifier from S3" }
            false
        }
    }

    /**
     * Generates a presigned URL for accessing the uploaded image.
     *
     * @param key The S3 key of the image
     * @return The presigned URL as a string
     */
    private fun generatePresignedUrl(key: String): String {
        // For public buckets, you can construct the URL directly
        // For private buckets, you may want to use a presigner for temporary access
        return "https://$bucket.s3.$region.amazonaws.com/$key"
    }

    /**
     * Extracts the S3 key from a S3 URL.
     *
     * @param url The S3 URL
     * @return The S3 key if found, null otherwise
     */
    override fun extractKeyFromUrl(url: String): String? {
        // Example URL: https://bucket.s3.region.amazonaws.com/wedding_photos/abc123.jpg
        val regex = ".*amazonaws.com/(.+)$".toRegex()
        return regex.find(url)?.groupValues?.get(1)
    }
}
