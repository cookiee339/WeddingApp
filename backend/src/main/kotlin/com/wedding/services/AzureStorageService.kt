package com.wedding.services

import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.wedding.config.AzureStorageConfig
import mu.KotlinLogging
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

/**
 * Service for interacting with Azure Blob Storage for image uploads.
 */
class AzureStorageService(config: AzureStorageConfig) : StorageService {
    private val logger = KotlinLogging.logger {}
    private val connectionString = config.connectionString
    private val containerName = config.containerName
    private val containerClient: BlobContainerClient = BlobServiceClientBuilder()
        .connectionString(connectionString)
        .buildClient()
        .getBlobContainerClient(containerName)

    init {
        // Ensure the container exists
        if (!containerClient.exists()) {
            logger.info { "Creating container $containerName" }
            containerClient.create()
        }
    }

    /**
     * Uploads an image to Azure Blob Storage.
     *
     * @param imageStream The input stream of the image file
     * @param filename The name of the file (for reference only)
     * @return The URL of the uploaded image if successful, null otherwise
     */
    override suspend fun uploadImage(imageStream: InputStream, filename: String): String? {
        return try {
            // Generate a unique blob name to avoid conflicts
            val fileExtension = filename.substringAfterLast('.', "jpg").lowercase()
            val uniqueId = UUID.randomUUID().toString()
            val blobName = "wedding_photos/$uniqueId.$fileExtension"

            val blobClient = containerClient.getBlobClient(blobName)

            // Read the stream into a byte array to get the actual content length
            val imageBytes = imageStream.readAllBytes()
            val contentLength = imageBytes.size.toLong()

            // Create a new ByteArrayInputStream from the bytes
            val byteArrayInputStream = ByteArrayInputStream(imageBytes)

            // Upload the image with the correct content length
            blobClient.upload(byteArrayInputStream, contentLength, true)

            val url = blobClient.blobUrl
            logger.info { "Successfully uploaded image $filename as $blobName to Azure Blob Storage at $url (size: $contentLength bytes)" }
            url
        } catch (e: Exception) {
            logger.error(e) { "Failed to upload image $filename to Azure Blob Storage" }
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
     * Deletes an image from Azure Blob Storage.
     *
     * @param identifier The blob URL or name of the image to delete
     * @return True if deletion was successful, false otherwise
     */
    override suspend fun deleteImage(identifier: String): Boolean {
        return try {
            // If the identifier is a URL, extract the blob name
            val blobName = if (identifier.startsWith("http")) {
                extractKeyFromUrl(identifier) ?: return false
            } else {
                identifier
            }

            val blobClient = containerClient.getBlobClient(blobName)
            val result = blobClient.deleteIfExists()

            if (result) {
                logger.info { "Successfully deleted image $blobName from Azure Blob Storage" }
            } else {
                logger.warn { "Image $blobName not found in Azure Blob Storage" }
            }

            result
        } catch (e: Exception) {
            logger.error(e) { "Error deleting image $identifier from Azure Blob Storage" }
            false
        }
    }

    /**
     * Extracts the blob name from an Azure Blob Storage URL.
     *
     * @param url The Azure Blob Storage URL
     * @return The blob name if found, null otherwise
     */
    override fun extractKeyFromUrl(url: String): String? {
        // Example URL: https://accountname.blob.core.windows.net/containername/wedding_photos/abc123.jpg
        val regex = ".*/$containerName/(.+)$".toRegex()
        return regex.find(url)?.groupValues?.get(1)
    }
}
