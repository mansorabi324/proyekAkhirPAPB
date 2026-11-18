package com.example.satuhati.data.repository

import com.example.satuhati.core.common.Resource
import com.example.satuhati.data.local.dao.CoupleDao
import com.example.satuhati.data.local.dao.UserDao
import com.example.satuhati.data.local.entity.CoupleEntity
import com.example.satuhati.data.local.entity.UserEntity
import com.example.satuhati.data.remote.service.FirebaseAuthService
import com.example.satuhati.data.remote.service.FirestoreCoupleService
import com.example.satuhati.domain.model.Couple
import com.example.satuhati.domain.model.CoupleStatus
import com.example.satuhati.domain.model.User
import com.example.satuhati.domain.model.UserRole
import com.example.satuhati.domain.repository.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.UUID

class AuthRepositoryImpl(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreCoupleService,
    private val userDao: UserDao,
    private val coupleDao: CoupleDao
) : IAuthRepository {

    override fun loginWithEmail(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val firebaseUser = authService.loginWithEmail(email, password)
            var user = firestoreService.getUser(firebaseUser.uid)
            if (user == null) {
                user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: email.substringBefore("@"),
                    email = email,
                    role = UserRole.ISTRI
                )
                firestoreService.saveUser(user)
            }
            withContext(Dispatchers.IO) {
                userDao.insertUser(UserEntity.fromDomain(user))
                user.coupleId?.let { cId ->
                    firestoreService.getCouple(cId)?.let { couple ->
                        coupleDao.insertCouple(CoupleEntity.fromDomain(couple))
                    }
                }
            }

            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal masuk ke akun."))
        }
    }

    override fun sendSignInLinkToEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            authService.sendSignInLinkToEmail(email)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal mengirimkan tautan masuk ke email."))
        }
    }

    override fun registerWithEmail(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val firebaseUser = authService.registerWithEmail(email, password)
            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                role = role
            )
            firestoreService.saveUser(user)
            withContext(Dispatchers.IO) {
                userDao.insertUser(UserEntity.fromDomain(user))
            }
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Pendaftaran gagal."))
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getFirstUser().map { it?.toDomain() }
    }

    override fun generatePairingCode(user: User): Flow<Resource<Couple>> = flow {
        emit(Resource.Loading)
        try {
            val code = generateRandom6DigitCode()
            val coupleId = user.coupleId ?: UUID.randomUUID().toString()
            val couple = Couple(
                id = coupleId,
                wifeId = user.id,
                wifeName = user.name,
                wifeEmail = user.email,
                pairingCode = code,
                status = CoupleStatus.CODE_GENERATED
            )
            firestoreService.createOrUpdateCouple(couple)
            withContext(Dispatchers.IO) {
                coupleDao.insertCouple(CoupleEntity.fromDomain(couple))
            }

            // Update user with coupleId & pairingCode
            val updatedUser = user.copy(coupleId = coupleId, pairingCode = code)
            firestoreService.saveUser(updatedUser)
            withContext(Dispatchers.IO) {
                userDao.insertUser(UserEntity.fromDomain(updatedUser))
            }

            emit(Resource.Success(couple))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal membuat kode pasangan."))
        }
    }

    override fun requestLinkWithCode(husband: User, code: String): Flow<Resource<Couple>> = flow {
        emit(Resource.Loading)
        try {
            val couple = firestoreService.getCoupleByCode(code)
                ?: throw Exception("Kode undangan tidak ditemukan. Pastikan kode dimasukkan dengan benar.")

            val updatedCouple = couple.copy(
                husbandId = husband.id,
                husbandName = husband.name,
                husbandEmail = husband.email,
                status = CoupleStatus.PENDING_APPROVAL
            )
            firestoreService.createOrUpdateCouple(updatedCouple)
            withContext(Dispatchers.IO) {
                coupleDao.insertCouple(CoupleEntity.fromDomain(updatedCouple))
            }

            val updatedHusband = husband.copy(
                coupleId = couple.id,
                partnerId = couple.wifeId,
                partnerName = couple.wifeName
            )
            firestoreService.saveUser(updatedHusband)
            withContext(Dispatchers.IO) {
                userDao.insertUser(UserEntity.fromDomain(updatedHusband))
            }

            emit(Resource.Success(updatedCouple))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal menautkan kode pasangan."))
        }
    }

    override fun acceptLinkRequest(coupleId: String): Flow<Resource<Couple>> = flow {
        emit(Resource.Loading)
        try {
            val couple = firestoreService.getCouple(coupleId)
                ?: throw Exception("Data pasangan tidak ditemukan.")

            val connectedCouple = couple.copy(status = CoupleStatus.CONNECTED)
            firestoreService.createOrUpdateCouple(connectedCouple)
            withContext(Dispatchers.IO) {
                coupleDao.insertCouple(CoupleEntity.fromDomain(connectedCouple))
            }

            // Update wife's partner info
            val wife = firestoreService.getUser(couple.wifeId)
            if (wife != null) {
                val updatedWife = wife.copy(
                    partnerId = couple.husbandId,
                    partnerName = couple.husbandName,
                    coupleId = coupleId
                )
                firestoreService.saveUser(updatedWife)
                withContext(Dispatchers.IO) {
                    userDao.insertUser(UserEntity.fromDomain(updatedWife))
                }
            }

            // Update husband's partner info
            couple.husbandId?.let { hId ->
                val husband = firestoreService.getUser(hId)
                if (husband != null) {
                    val updatedHusband = husband.copy(
                        partnerId = couple.wifeId,
                        partnerName = couple.wifeName,
                        coupleId = coupleId
                    )
                    firestoreService.saveUser(updatedHusband)
                }
            }

            emit(Resource.Success(connectedCouple))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal menerima permintaan."))
        }
    }

    override fun rejectLinkRequest(coupleId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val couple = firestoreService.getCouple(coupleId)
                ?: throw Exception("Data pasangan tidak ditemukan.")

            val resetCouple = couple.copy(
                husbandId = null,
                husbandName = null,
                husbandEmail = null,
                status = CoupleStatus.CODE_GENERATED
            )
            firestoreService.createOrUpdateCouple(resetCouple)
            withContext(Dispatchers.IO) {
                coupleDao.insertCouple(CoupleEntity.fromDomain(resetCouple))
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal menolak permintaan."))
        }
    }

    override fun observeCouple(coupleId: String): Flow<Couple?> {
        return firestoreService.observeCouple(coupleId)
    }

    override fun observeUser(userId: String): Flow<User?> {
        return firestoreService.observeUser(userId)
    }

    override suspend fun logout() {
        authService.signOut()
        withContext(Dispatchers.IO) {
            userDao.clearUsers()
            coupleDao.clearCouples()
        }
    }

    private fun generateRandom6DigitCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val random = SecureRandom()
        return (1..6)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }
}
