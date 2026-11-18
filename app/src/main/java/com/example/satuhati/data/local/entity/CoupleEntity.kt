package com.example.satuhati.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.satuhati.domain.model.Couple
import com.example.satuhati.domain.model.CoupleStatus

@Entity(tableName = "couples")
data class CoupleEntity(
    @PrimaryKey
    val id: String,
    val wifeId: String,
    val wifeName: String,
    val wifeEmail: String,
    val husbandId: String?,
    val husbandName: String?,
    val husbandEmail: String?,
    val pairingCode: String,
    val status: String,
    val createdAt: Long
) {
    fun toDomain(): Couple {
        return Couple(
            id = id,
            wifeId = wifeId,
            wifeName = wifeName,
            wifeEmail = wifeEmail,
            husbandId = husbandId,
            husbandName = husbandName,
            husbandEmail = husbandEmail,
            pairingCode = pairingCode,
            status = try { CoupleStatus.valueOf(status) } catch (e: Exception) { CoupleStatus.CODE_GENERATED },
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(couple: Couple): CoupleEntity {
            return CoupleEntity(
                id = couple.id,
                wifeId = couple.wifeId,
                wifeName = couple.wifeName,
                wifeEmail = couple.wifeEmail,
                husbandId = couple.husbandId,
                husbandName = couple.husbandName,
                husbandEmail = couple.husbandEmail,
                pairingCode = couple.pairingCode,
                status = couple.status.name,
                createdAt = couple.createdAt
            )
        }
    }
}
