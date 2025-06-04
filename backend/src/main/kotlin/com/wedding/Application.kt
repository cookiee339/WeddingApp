package com.wedding

import com.typesafe.config.ConfigFactory
import com.wedding.config.AwsS3Config
import com.wedding.config.CloudinaryConfig
import com.wedding.config.DatabaseConfig
import com.wedding.database.DatabaseFactory
import com.wedding.plugins.configureAccessRoutes
import com.wedding.plugins.configureCORS
import com.wedding.plugins.configureRouting
import com.wedding.plugins.configureSerialization
import com.wedding.plugins.configureStatusPages
import com.wedding.services.AwsS3Service
import com.wedding.services.CloudinaryService
import com.wedding.services.PhotoService
import com.wedding.services.StorageService
import io.github.config4k.extract
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    // Load configuration
    val config = ConfigFactory.load()

    // Extract database configuration
    val dbConfig = config.extract<DatabaseConfig>("database")

    // Extract storage backend type
    val storageBackend = config.getString("storage.backend")

    // Initialize database connection
    DatabaseFactory.init(dbConfig)

    // Initialize storage service
    val storageService: StorageService = when (storageBackend.lowercase()) {
        "s3" -> {
            val s3Config = config.extract<AwsS3Config>("awsS3")
            AwsS3Service(s3Config)
        }
        else -> { // default to cloudinary
            val cloudinaryConfig = config.extract<CloudinaryConfig>("cloudinary")
            CloudinaryService(cloudinaryConfig)
        }
    }

    // Create photo service
    val photoService = PhotoService()

    // Start server
    embeddedServer(Netty, port = config.extract("ktor.deployment.port"), host = "0.0.0.0") {
        module(storageService, photoService)
    }.start(wait = true)
}

fun Application.module(storageService: StorageService, photoService: PhotoService) {
    // Configure plugins
    configureSerialization()
    configureCORS()
    configureAccessRoutes()
    configureRouting(storageService, photoService)
    configureStatusPages()
}
