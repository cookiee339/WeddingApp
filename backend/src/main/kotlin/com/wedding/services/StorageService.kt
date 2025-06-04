package com.wedding.services

import java.io.InputStream

interface StorageService {
    suspend fun uploadImage(imageStream: InputStream, filename: String): String?
    suspend fun deleteImage(identifier: String): Boolean
    fun extractKeyFromUrl(url: String): String?
} 