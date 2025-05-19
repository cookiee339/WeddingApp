package com.wedding.config

data class DatabaseConfig(
    val driverClassName: String,
    val jdbcURL: String,
    val username: String,
    val password: String,
    val maxPoolSize: Int = 3
)

data class CloudinaryConfig(
    val cloudName: String,
    val apiKey: String,
    val apiSecret: String
)