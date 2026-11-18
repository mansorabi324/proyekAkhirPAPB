package com.example.satuhati.data.remote.service

import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun loginWithEmail(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Login gagal, pengguna tidak ditemukan.")
    }

    suspend fun registerWithEmail(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Pendaftaran gagal.")
    }

    suspend fun sendSignInLinkToEmail(email: String) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://satuhati-48d2d.firebaseapp.com/finishSignIn?email=$email")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.satuhati", true, "1")
            .build()
        auth.sendSignInLinkToEmail(email, actionCodeSettings).await()
    }

    suspend fun signInWithEmailLink(email: String, emailLink: String): FirebaseUser {
        val result = auth.signInWithEmailLink(email, emailLink).await()
        return result.user ?: throw Exception("Login tautan email gagal.")
    }

    fun isSignInWithEmailLink(link: String): Boolean {
        return auth.isSignInWithEmailLink(link)
    }

    fun signOut() {
        auth.signOut()
    }
}
