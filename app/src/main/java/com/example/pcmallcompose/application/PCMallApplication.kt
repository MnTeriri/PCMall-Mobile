package com.example.pcmallcompose.application

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class PCMallApplication : Application() {
    companion object {
        const val TAG = "PCMallApplication"
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "PCMallApplication启动")
        val remember: Boolean = sharedPreferences.getBoolean("remember", false)
        if (!remember) {
            Log.d(TAG, "用户没有记住账号，清空token和登录信息")
            sharedPreferences.edit {
                putString("data", null)
                putString("token", null)
                putBoolean("remember", false)
            }
        }
//        val editor = sharedPreferences.edit()
//        editor.putString("data", null);
//        editor.putString("token", null);
//        editor.apply();
    }
}