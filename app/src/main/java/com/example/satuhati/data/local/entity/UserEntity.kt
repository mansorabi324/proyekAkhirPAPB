package com.example.satuhati.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.satuhati.domain.model.User
import com.example.satuhati.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val coupleId: String?,
    val partnerId: String?,
    val partnerName: String?,
    val pairingCode: String?,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.ISTRI },
            coupleId = coupleId,
            partnerId = partnerId,
            partnerName = partnerName,
            pairingCode = pairingCode
        )
    }

    companion object {
        fun fromDomain(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                role = user.role.name,
                coupleId = user.coupleId,
                partnerId = user.partnerId,
                partnerName = user.partnerName,
                pairingCode = user.pairingCode
            )
        }
    }
}
