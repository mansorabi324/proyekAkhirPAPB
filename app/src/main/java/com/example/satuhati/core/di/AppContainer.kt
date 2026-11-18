package com.example.satuhati.core.di

import android.content.Context
import com.example.satuhati.data.local.SatuHatiDatabase
import com.example.satuhati.data.remote.service.FirebaseAuthService
import com.example.satuhati.data.remote.service.FirestoreCoupleService
import com.example.satuhati.data.repository.AuthRepositoryImpl
import com.example.satuhati.domain.repository.IAuthRepository

class AppContainer(context: Context) {
    val database: SatuHatiDatabase by lazy {
        SatuHatiDatabase.getInstance(context)
    }

    val firebaseAuthService: FirebaseAuthService by lazy {
        FirebaseAuthService()
    }

    val firestoreCoupleService: FirestoreCoupleService by lazy {
        FirestoreCoupleService()
    }

    val authRepository: IAuthRepository by lazy {
        AuthRepositoryImpl(
            authService = firebaseAuthService,
            firestoreService = firestoreCoupleService,
            userDao = database.userDao(),
            coupleDao = database.coupleDao()
        )
    }
}
