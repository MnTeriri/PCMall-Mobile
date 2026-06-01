package com.example.pcmallcompose.application

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.core.content.edit
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString
import com.example.pcmallcompose.core.model.User
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Compose: compositionLocalOf
val LocalUserData = compositionLocalOf<User?> { null }

@Singleton
class UserSession @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val TAG = "UserSession"
    }

    private val _user = MutableStateFlow(loadFromDisk())
    val user: StateFlow<User?> = _user.asStateFlow()

    // Application 启动时调用一次，处理"记住我"逻辑
    fun onAppStart() {
        val remember = sharedPreferences.getBoolean("remember", false)
        if (!remember) {
            Log.d(TAG, "用户没有记住账号，清空token和登录信息")
            onLogout()
        }
    }

    // 登录成功后调用
    fun onLogin(user: User?, token: String, remember: Boolean) {
        sharedPreferences.edit {
            putString("data", user.toJSONString())
            putString("token", token)
            putBoolean("remember", remember)
        }
        _user.value = user
    }

    // 登出
    fun onLogout() {
        sharedPreferences.edit {
            putString("data", null)
            putString("token", null)
            putBoolean("remember", false)
        }
        _user.value = null
    }

    // 从 SharedPreferences 重新读取到内存
    fun refreshFromDisk() {
        _user.value = loadFromDisk()
    }

    private fun loadFromDisk(): User? {
        val data = sharedPreferences.getString("data", null)
        return data.parseObject<User>()
    }

    fun onCartChanged() {
        val current = sharedPreferences.getInt("cart_version", 0)
        sharedPreferences.edit { putInt("cart_version", current + 1) }
    }

    fun cartVersion(): Int = sharedPreferences.getInt("cart_version", 0)
}