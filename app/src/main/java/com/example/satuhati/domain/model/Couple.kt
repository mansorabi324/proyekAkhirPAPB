package com.example.satuhati.domain.model

enum class CoupleStatus {
    IDLE,
    CODE_GENERATED,
    PENDING_APPROVAL,
    CONNECTED,
    REJECTED
}

data class Couple(
    val id: String = "",
    val wifeId: String = "",
    val wifeName: String = "",
    val wifeEmail: String = "",
    val husbandId: String? = null,
    val husbandName: String? = null,
    val husbandEmail: String? = null,
    val pairingCode: String = "",
    val status: CoupleStatus = CoupleStatus.CODE_GENERATED,
    val createdAt: Long = System.currentTimeMillis()
)
