package com.example.pcmallcompose.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.core.model.response.ResponseCode
import com.example.pcmallcompose.core.network.service.LoginRegisterService
import com.example.pcmallcompose.core.network.utils.RetrofitUtils
import com.example.pcmallcompose.utils.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class LoginUiState(
    val isLoading: Boolean = false,          // 验证码加载中
    val captchaImage: ImageBitmap? = null,
    val isLoggingIn: Boolean = false,        // 登录请求中
    val isUserLoggedIn: Boolean = false,     // 登录成功，触发导航
    val errorMessage: ErrorMessage? = null,        // 瞬态错误，UI 展示后回调清空
    val shouldRefreshCaptcha: Boolean = false, // 验证码错误，需要刷新
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRegisterService: LoginRegisterService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    companion object {
        private const val TAG: String = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(LoginUiState())//MutableStateFlow（可写，Kotlin类）
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow() //StateFlow（只读，Kotlin类）

    private fun catchHttpException(e: HttpException) {
        val response = RetrofitUtils.getErrorMessage(e)!!
        Log.e(TAG, "$e: $response", e)

        val errorMessage = when (response.code) {
            ResponseCode.CAPTCHA_ERROR.code -> ErrorMessage.Toast("验证码错误！")
            ResponseCode.ACCOUNT_ERROR.code -> ErrorMessage.Dialog("账号或密码错误！")
            else -> ErrorMessage.Toast(response.message)
        }

        _uiState.update {
            it.copy(
                isLoggingIn = false,
                isLoading = false,
                errorMessage = errorMessage,
                shouldRefreshCaptcha = response.code == ResponseCode.CAPTCHA_ERROR.code,
            )
        }
    }

    private fun catchException(e: Exception) {
        Log.e(TAG, e.toString(), e)
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = ErrorMessage.Toast("${e.message}")
            )
        }
    }

    // UI 展示完瞬态消息后回调，清空该字段
    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun getCaptcha() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, captchaImage = null, shouldRefreshCaptcha = false) }
            try {
                delay(1000)
                val imageBitmap = ImageUtils.decodeImageString(loginRegisterService.getCaptcha().data!!)

                _uiState.update { it.copy(isLoading = false, captchaImage = imageBitmap) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun login(uid: String, password: String, code: String, remember: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoggingIn = true, errorMessage = null) }
            try {
                val loginResponse = loginRegisterService.login(uid, password, code)
                Log.d(TAG, "登录成功：$loginResponse")
                sharedPreferences.edit {
                    putString("data", JSON.toJSONString(loginResponse.data))
                    putString("token", loginResponse.message)
                    putBoolean("remember", remember)
                }
                _uiState.update { it.copy(isLoggingIn = false, isUserLoggedIn = true) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }
}