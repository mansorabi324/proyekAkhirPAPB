package com.example.satuhati

import android.app.Application
import com.example.satuhati.core.di.AppContainer

class SatuHatiApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
