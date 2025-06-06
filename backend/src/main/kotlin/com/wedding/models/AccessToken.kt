package com.wedding.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object AccessTokens : IntIdTable() {
    val token = varchar("token", 255).uniqueIndex()
    val description = varchar("description", 500).nullable()
    val createdAt = datetime("created_at")
    val expiresAt = datetime("expires_at")
    val isActive = bool("is_active").default(true)
}

class AccessToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccessToken>(AccessTokens)

    var token by AccessTokens.token
    var description by AccessTokens.description
    var createdAt by AccessTokens.createdAt
    var expiresAt by AccessTokens.expiresAt
    var isActive by AccessTokens.isActive
}

@Serializable
data class AccessTokenResponse(
    val id: String,
    val token: String,
    val description: String?,
    val createdAt: String,
    val expiresAt: String,
    val isActive: Boolean,
)

@Serializable
data class CreateAccessTokenRequest(
    val description: String? = null,
    val validityHours: Int = 48,
)

@Serializable
data class ValidateTokenRequest(
    val token: String,
)

@Serializable
data class ValidateTokenResponse(
    val valid: Boolean,
)
