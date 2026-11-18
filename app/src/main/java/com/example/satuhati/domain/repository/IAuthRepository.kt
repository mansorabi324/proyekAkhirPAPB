package com.example.satuhati.domain.repository

import com.example.satuhati.core.common.Resource
import com.example.satuhati.domain.model.Couple
import com.example.satuhati.domain.model.User
import com.example.satuhati.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun loginWithEmail(email: String, password: String): Flow<Resource<User>>
    fun sendSignInLinkToEmail(email: String): Flow<Resource<Unit>>
    fun registerWithEmail(name: String, email: String, password: String, role: UserRole): Flow<Resource<User>>
    fun getCurrentUser(): Flow<User?>
    fun generatePairingCode(user: User): Flow<Resource<Couple>>
    fun requestLinkWithCode(husband: User, code: String): Flow<Resource<Couple>>
    fun acceptLinkRequest(coupleId: String): Flow<Resource<Couple>>
    fun rejectLinkRequest(coupleId: String): Flow<Resource<Unit>>
    fun observeCouple(coupleId: String): Flow<Couple?>
    fun observeUser(userId: String): Flow<User?>
    suspend fun logout()
}
