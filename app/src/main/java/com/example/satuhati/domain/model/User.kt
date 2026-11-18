package com.example.satuhati.domain.model

enum class UserRole {
    ISTRI,
    SUAMI
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.ISTRI,
    val coupleId: String? = null,
    val partnerId: String? = null,
    val partnerName: String? = null,
    val pairingCode: String? = null
)
