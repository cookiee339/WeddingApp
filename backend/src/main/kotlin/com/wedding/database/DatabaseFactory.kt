package com.wedding.database

import com.wedding.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.wedding.database.tables.Photos
import mu.KotlinLogging

object DatabaseFactory {
    private val logger = KotlinLogging.logger {}

    fun init(config: DatabaseConfig) {
        // Create a connection pool
        val hikariConfig = HikariConfig().apply {
            driverClassName = config.driverClassName
            jdbcUrl = config.jdbcURL
            username = config.username
            password = config.password
            maximumPoolSize = config.maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        val database = Database.connect(dataSource)

        // Initialize tables
        transaction(database) {
            SchemaUtils.create(Photos)
            logger.info { "Database tables initialized" }
        }
    }

    // Utility function to run database operations in a coroutine context
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}