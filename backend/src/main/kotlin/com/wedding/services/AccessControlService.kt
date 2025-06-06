package com.wedding.services

import com.wedding.models.AccessToken
import com.wedding.models.AccessTokenResponse
import com.wedding.models.AccessTokens
import com.wedding.models.CreateAccessTokenRequest
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AccessControlService {

    private val secureRandom = SecureRandom()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun generateToken(request: CreateAccessTokenRequest): AccessTokenResponse {
        return transaction {
            val token = generateSecureToken()
            val now = LocalDateTime.now()
            val expiresAt = now.plusHours(request.validityHours.toLong())

            val accessToken = AccessToken.new {
                this.token = token
                this.description = request.description
                this.createdAt = now
                this.expiresAt = expiresAt
                this.isActive = true
            }

            AccessTokenResponse(
                id = accessToken.id.value.toString(),
                token = accessToken.token,
                description = accessToken.description,
                createdAt = accessToken.createdAt.format(dateFormatter),
                expiresAt = accessToken.expiresAt.format(dateFormatter),
                isActive = accessToken.isActive,
            )
        }
    }

    fun validateToken(token: String): Boolean {
        return transaction {
            val accessToken = AccessToken.find { AccessTokens.token eq token }.firstOrNull()

            accessToken?.let {
                it.isActive && LocalDateTime.now().isBefore(it.expiresAt)
            } ?: false
        }
    }

    fun getAllTokens(): List<AccessTokenResponse> {
        return transaction {
            AccessToken.all().map { token ->
                AccessTokenResponse(
                    id = token.id.value.toString(),
                    token = token.token,
                    description = token.description,
                    createdAt = token.createdAt.format(dateFormatter),
                    expiresAt = token.expiresAt.format(dateFormatter),
                    isActive = token.isActive,
                )
            }.sortedByDescending { it.createdAt }
        }
    }

    fun deactivateToken(tokenId: Int): Boolean {
        return transaction {
            val accessToken = AccessToken.findById(tokenId)
            accessToken?.let {
                it.isActive = false
                true
            } ?: false
        }
    }

    fun cleanupExpiredTokens(): Int {
        return transaction {
            val now = LocalDateTime.now()
            val expiredTokens = AccessToken.find {
                AccessTokens.expiresAt less now
            }

            val count = expiredTokens.count().toInt()
            expiredTokens.forEach { it.isActive = false }
            count
        }
    }

    private fun generateSecureToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
