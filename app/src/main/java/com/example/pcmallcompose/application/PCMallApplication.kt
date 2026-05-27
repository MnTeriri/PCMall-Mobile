package com.example.pcmallcompose.application

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class PCMallApplication : Application() {
    companion object {
        const val TAG = "PCMallApplication"
    }

    @Inject
    lateinit var userSession: UserSession

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "PCMallApplication启动")
        userSession.onAppStart()
    }
}