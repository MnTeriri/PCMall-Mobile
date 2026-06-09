package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.repository.AuthRepository
import com.example.pcmallcompose.core.model.response.ResponseCode
import com.example.pcmallcompose.ui.ErrorMessage
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
import kotlin.time.Duration.Companion.milliseconds

data class LoginUiState(
    val isLoading: Boolean = false,             // 验证码加载中
    val captchaImage: ImageBitmap? = null,
    val isLoggingIn: Boolean = false,           // 登录请求中
    val isUserLoggedIn: Boolean = false,        // 登录成功，触发导航
    val errorMessage: ErrorMessage? = null,     // 瞬态错误，UI 展示后回调清空
    val shouldRefreshCaptcha: Boolean = false,  // 验证码错误，需要刷新
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        private const val TAG: String = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(LoginUiState()) // MutableStateFlow（可写，Kotlin类）
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow() // StateFlow（只读，Kotlin类）

    fun getCaptcha() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, captchaImage = null, shouldRefreshCaptcha = false) }
            delay(1000.milliseconds)
            authRepository.getCaptcha()
                .mapCatching { it?.let(ImageUtils::decodeImageString) }
                .onSuccess { image ->
                    _uiState.update { it.copy(isLoading = false, captchaImage = image) }
                }
                .onFailure { handleError(it) }
        }
    }

    fun login(uid: String, password: String, code: String, remember: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoggingIn = true, errorMessage = null) }
            authRepository.login(uid, password, code)
                .onSuccess { (user, token) ->
                    Log.d(TAG, "登录成功 user: $user, token: $token")
                    userSession.onLogin(user = user, token = token, remember = remember)
                    _uiState.update { it.copy(isLoggingIn = false, isUserLoggedIn = true) }
                }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(e: Throwable) {
        when (e) {
            is ApiException -> {
                Log.w(TAG, e.toString(), e)
                val (errorMessage, needRefresh) = when (e.code) {
                    ResponseCode.CAPTCHA_ERROR.code -> ErrorMessage.Toast("验证码错误！") to true
                    ResponseCode.ACCOUNT_ERROR.code -> ErrorMessage.Dialog("账号或密码错误！") to true
                    else -> ErrorMessage.Toast(e.message) to true
                }
                _uiState.update {
                    it.copy(
                        isLoggingIn = false,
                        isLoading = false,
                        errorMessage = errorMessage,
                        shouldRefreshCaptcha = needRefresh
                    )
                }
            }
            else -> _uiState.update {
                Log.e(TAG, e.toString(), e)
                it.copy(isLoggingIn = false, isLoading = false, errorMessage = ErrorMessage.Toast("${e.message}"))
            }
        }
    }

    // UI 展示完瞬态消息后回调，清空该字段
    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}