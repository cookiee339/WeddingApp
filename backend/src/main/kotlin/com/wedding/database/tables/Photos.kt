package com.wedding.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Photos : Table() {
    val photoId = integer("photo_id").autoIncrement()
    val imageUrl = text("image_url")
    val uploaderId = text("uploader_id")
    val uploadedAt = timestamp("uploaded_at")

    override val primaryKey = PrimaryKey(photoId)
}
