package com.example.pcmallcompose.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.User
import com.example.pcmallcompose.ui.page.MainActivityPage
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val LocalUserData = compositionLocalOf<User?> { null }

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var userData by mutableStateOf<User?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PCMallComposeTheme {
                CompositionLocalProvider(LocalUserData provides userData) {
                    MainActivityPage()
                }
            }
        }
    }

    fun getUserData() {
        val data = sharedPreferences.getString("data", null)
        if (data == null) {
            Log.w(TAG, "用户没登陆")
            userData = null
            return
        }
        userData = JSON.parseObject(data, User::class.java)
    }

    fun logout() {
        val editor = sharedPreferences.edit()
        editor.putString("data", null)
        editor.putString("token", null)
        editor.putBoolean("remember", false)
        editor.apply()
        userData = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        getUserData()
        Log.d(TAG, "登录用户：$userData")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }
}