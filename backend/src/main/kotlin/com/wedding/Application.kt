package com.wedding

import com.typesafe.config.ConfigFactory
import com.wedding.config.CloudinaryConfig
import com.wedding.config.DatabaseConfig
import com.wedding.database.DatabaseFactory
import com.wedding.plugins.configureCORS
import com.wedding.plugins.configureRouting
import com.wedding.plugins.configureSerialization
import com.wedding.plugins.configureStatusPages
import com.wedding.services.CloudinaryService
import com.wedding.services.PhotoService
import io.github.config4k.extract
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    // Load configuration
    val config = ConfigFactory.load()

    // Extract database configuration
    val dbConfig = config.extract<DatabaseConfig>("database")

    // Extract Cloudinary configuration
    val cloudinaryConfig = config.extract<CloudinaryConfig>("cloudinary")

    // Initialize database connection
    DatabaseFactory.init(dbConfig)

    // Initialize Cloudinary
    val cloudinaryService = CloudinaryService(cloudinaryConfig)

    // Create photo service
    val photoService = PhotoService()

    // Start server
    embeddedServer(Netty, port = config.extract("ktor.deployment.port"), host = "0.0.0.0") {
        module(cloudinaryService, photoService)
    }.start(wait = true)
}

fun Application.module(cloudinaryService: CloudinaryService, photoService: PhotoService) {
    // Configure plugins
    configureSerialization()
    configureRouting(cloudinaryService, photoService)
    configureCORS()
    configureStatusPages()
}
