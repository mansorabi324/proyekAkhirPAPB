package com.example.satuhati.data.remote.service

import com.example.satuhati.domain.model.Couple
import com.example.satuhati.domain.model.CoupleStatus
import com.example.satuhati.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreCoupleService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")
    private val couplesCollection = firestore.collection("couples")

    suspend fun saveUser(user: User) {
        val userMap = hashMapOf(
            "id" to user.id,
            "name" to user.name,
            "email" to user.email,
            "role" to user.role.name,
            "coupleId" to user.coupleId,
            "partnerId" to user.partnerId,
            "partnerName" to user.partnerName,
            "pairingCode" to user.pairingCode
        )
        usersCollection.document(user.id).set(userMap).await()
    }

    suspend fun getUser(userId: String): User? {
        val doc = usersCollection.document(userId).get().await()
        if (!doc.exists()) return null
        return User(
            id = doc.getString("id") ?: userId,
            name = doc.getString("name") ?: "",
            email = doc.getString("email") ?: "",
            role = try { com.example.satuhati.domain.model.UserRole.valueOf(doc.getString("role") ?: "ISTRI") } catch (e: Exception) { com.example.satuhati.domain.model.UserRole.ISTRI },
            coupleId = doc.getString("coupleId"),
            partnerId = doc.getString("partnerId"),
            partnerName = doc.getString("partnerName"),
            pairingCode = doc.getString("pairingCode")
        )
    }

    fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val listener: ListenerRegistration = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = User(
                        id = snapshot.getString("id") ?: userId,
                        name = snapshot.getString("name") ?: "",
                        email = snapshot.getString("email") ?: "",
                        role = try { com.example.satuhati.domain.model.UserRole.valueOf(snapshot.getString("role") ?: "ISTRI") } catch (e: Exception) { com.example.satuhati.domain.model.UserRole.ISTRI },
                        coupleId = snapshot.getString("coupleId"),
                        partnerId = snapshot.getString("partnerId"),
                        partnerName = snapshot.getString("partnerName"),
                        pairingCode = snapshot.getString("pairingCode")
                    )
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun createOrUpdateCouple(couple: Couple) {
        val coupleMap = hashMapOf(
            "id" to couple.id,
            "wifeId" to couple.wifeId,
            "wifeName" to couple.wifeName,
            "wifeEmail" to couple.wifeEmail,
            "husbandId" to couple.husbandId,
            "husbandName" to couple.husbandName,
            "husbandEmail" to couple.husbandEmail,
            "pairingCode" to couple.pairingCode,
            "status" to couple.status.name,
            "createdAt" to couple.createdAt
        )
        couplesCollection.document(couple.id).set(coupleMap).await()
    }

    suspend fun getCoupleByCode(code: String): Couple? {
        val snapshot = couplesCollection
            .whereEqualTo("pairingCode", code.uppercase().trim())
            .get()
            .await()

        if (snapshot.isEmpty) return null
        val doc = snapshot.documents.first()
        return Couple(
            id = doc.getString("id") ?: doc.id,
            wifeId = doc.getString("wifeId") ?: "",
            wifeName = doc.getString("wifeName") ?: "",
            wifeEmail = doc.getString("wifeEmail") ?: "",
            husbandId = doc.getString("husbandId"),
            husbandName = doc.getString("husbandName"),
            husbandEmail = doc.getString("husbandEmail"),
            pairingCode = doc.getString("pairingCode") ?: code,
            status = try { CoupleStatus.valueOf(doc.getString("status") ?: "CODE_GENERATED") } catch (e: Exception) { CoupleStatus.CODE_GENERATED },
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
        )
    }

    suspend fun getCouple(coupleId: String): Couple? {
        val doc = couplesCollection.document(coupleId).get().await()
        if (!doc.exists()) return null
        return Couple(
            id = doc.getString("id") ?: coupleId,
            wifeId = doc.getString("wifeId") ?: "",
            wifeName = doc.getString("wifeName") ?: "",
            wifeEmail = doc.getString("wifeEmail") ?: "",
            husbandId = doc.getString("husbandId"),
            husbandName = doc.getString("husbandName"),
            husbandEmail = doc.getString("husbandEmail"),
            pairingCode = doc.getString("pairingCode") ?: "",
            status = try { CoupleStatus.valueOf(doc.getString("status") ?: "CODE_GENERATED") } catch (e: Exception) { CoupleStatus.CODE_GENERATED },
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
        )
    }

    fun observeCouple(coupleId: String): Flow<Couple?> = callbackFlow {
        val listener: ListenerRegistration = couplesCollection.document(coupleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val couple = Couple(
                        id = snapshot.getString("id") ?: coupleId,
                        wifeId = snapshot.getString("wifeId") ?: "",
                        wifeName = snapshot.getString("wifeName") ?: "",
                        wifeEmail = snapshot.getString("wifeEmail") ?: "",
                        husbandId = snapshot.getString("husbandId"),
                        husbandName = snapshot.getString("husbandName"),
                        husbandEmail = snapshot.getString("husbandEmail"),
                        pairingCode = snapshot.getString("pairingCode") ?: "",
                        status = try { CoupleStatus.valueOf(snapshot.getString("status") ?: "CODE_GENERATED") } catch (e: Exception) { CoupleStatus.CODE_GENERATED },
                        createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                    trySend(couple)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }
}
