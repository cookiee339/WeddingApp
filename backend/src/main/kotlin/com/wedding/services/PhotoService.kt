package com.wedding.services

import com.wedding.database.DatabaseFactory.dbQuery
import com.wedding.database.tables.Photos
import com.wedding.models.Photo
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Service for managing photos in the database.
 */
class PhotoService {
    private val logger = KotlinLogging.logger {}
    private val isoFormatter = DateTimeFormatter.ISO_INSTANT

    /**
     * Gets all photos with optional filtering by uploader ID.
     *
     * @param uploaderId Optional ID to filter photos by uploader
     * @param page Page number (1-based) for pagination
     * @param limit Maximum number of photos to return per page
     * @return List of photos ordered by upload time (newest first)
     */
    suspend fun getAllPhotos(uploaderId: String? = null, page: Int = 1, limit: Int = 20): List<Photo> {
        return dbQuery {
            val query = if (uploaderId != null) {
                Photos.select { Photos.uploaderId eq uploaderId }
            } else {
                Photos.selectAll()
            }

            query
                .orderBy(Photos.uploadedAt, SortOrder.DESC)
                .limit(limit, offset = ((page - 1) * limit).toLong())
                .map { resultRow ->
                    Photo(
                        photoId = resultRow[Photos.photoId],
                        imageUrl = resultRow[Photos.imageUrl],
                        uploaderId = resultRow[Photos.uploaderId],
                        uploadedAt = resultRow[Photos.uploadedAt].toString(),
                    )
                }
        }
    }

    /**
     * Gets all photos with pagination metadata.
     *
     * @param uploaderId Optional ID to filter photos by uploader
     * @param page Page number (1-based) for pagination
     * @param limit Maximum number of photos to return per page
     * @return Pair of (list of photos, total count) for building paginated responses
     */
    suspend fun getAllPhotosWithCount(uploaderId: String? = null, page: Int = 1, limit: Int = 20): Pair<List<Photo>, Int> {
        val photos = getAllPhotos(uploaderId, page, limit)
        val count = countPhotos(uploaderId).toInt()
        return Pair(photos, count)
    }

    /**
     * Gets a photo by its ID.
     *
     * @param photoId The ID of the photo
     * @return The photo if found, null otherwise
     */
    suspend fun getPhotoById(photoId: Int): Photo? {
        return dbQuery {
            Photos.select { Photos.photoId eq photoId }
                .map { resultRow ->
                    Photo(
                        photoId = resultRow[Photos.photoId],
                        imageUrl = resultRow[Photos.imageUrl],
                        uploaderId = resultRow[Photos.uploaderId],
                        uploadedAt = resultRow[Photos.uploadedAt].toString(),
                    )
                }
                .singleOrNull()
        }
    }

    /**
     * Creates a new photo record in the database.
     *
     * @param imageUrl The URL to the image stored in Cloudinary
     * @param uploaderId The ID of the user who uploaded the photo
     * @return The newly created photo if successful, null otherwise
     */
    suspend fun createPhoto(imageUrl: String, uploaderId: String): Photo? {
        return dbQuery {
            val insertStatement = Photos.insert {
                it[Photos.imageUrl] = imageUrl
                it[Photos.uploaderId] = uploaderId
                it[Photos.uploadedAt] = Instant.now()
            }

            val resultRow = insertStatement.resultedValues?.singleOrNull()

            if (resultRow != null) {
                Photo(
                    photoId = resultRow[Photos.photoId],
                    imageUrl = resultRow[Photos.imageUrl],
                    uploaderId = resultRow[Photos.uploaderId],
                    uploadedAt = resultRow[Photos.uploadedAt].toString(),
                )
            } else {
                logger.error { "Failed to insert photo for uploader $uploaderId" }
                null
            }
        }
    }

    /**
     * Deletes a photo from the database.
     *
     * @param photoId ID of the photo to delete
     * @param uploaderId ID of the uploader (for verification)
     * @return True if photo was deleted, false otherwise
     */
    suspend fun deletePhoto(photoId: Int, uploaderId: String? = null): Boolean {
        return dbQuery {
            // Build the where clause
            val whereClause = if (uploaderId != null) {
                // Only delete if the photo belongs to the specified uploader
                (Photos.photoId eq photoId) and (Photos.uploaderId eq uploaderId)
            } else {
                // Delete any photo with this ID
                Photos.photoId eq photoId
            }

            // Perform the delete operation
            val deletedRows = Photos.deleteWhere { whereClause }

            // Return whether a row was deleted
            deletedRows > 0
        }
    }

    /**
     * Counts the total number of photos, optionally filtered by uploader ID.
     *
     * @param uploaderId Optional ID to filter by uploader
     * @return The total count of photos
     */
    suspend fun countPhotos(uploaderId: String? = null): Long {
        return dbQuery {
            if (uploaderId != null) {
                Photos.select { Photos.uploaderId eq uploaderId }.count()
            } else {
                Photos.selectAll().count()
            }
        }
    }
}
