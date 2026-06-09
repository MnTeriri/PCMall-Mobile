package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class RegisterUiState(
    val isLoading: Boolean = false,              // 验证码加载中
    val captchaImage: ImageBitmap? = null,
    val isRegistering: Boolean = false,          // 注册请求中
    val isUserRegistered: Boolean = false,       // 注册成功，触发导航
    val errorMessage: ErrorMessage? = null,      // 瞬态错误，UI 展示后回调清空
    val shouldRefreshCaptcha: Boolean = false,   // 验证码错误，需要刷新
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    companion object {
        private const val TAG: String = "RegisterViewModel"
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

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

    fun register(uid: String, password: String, code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isRegistering = true, errorMessage = null) }
            authRepository.register(uid, password, code)
                .onSuccess { _uiState.update { it.copy(isRegistering = false, isUserRegistered = true) } }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(e: Throwable) {
        Log.e(TAG, e.toString(), e)
        when (e) {
            is ApiException -> {
                val (errorMessage, needRefresh) = when (e.code) {
                    ResponseCode.CAPTCHA_ERROR.code -> ErrorMessage.Toast("验证码错误！") to true
                    ResponseCode.USER_EXIST_ERROR.code -> ErrorMessage.Dialog("账号已存在！") to true
                    else -> ErrorMessage.Toast(e.message) to true
                }
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        isLoading = false,
                        errorMessage = errorMessage,
                        shouldRefreshCaptcha = needRefresh
                    )
                }
            }
            else -> _uiState.update {
                it.copy(isRegistering = false, isLoading = false, errorMessage = ErrorMessage.Toast("${e.message}"))
            }
        }
    }

    // UI 展示完瞬态消息后回调，清空该字段
    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}