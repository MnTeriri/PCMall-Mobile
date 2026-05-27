package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val loginRegisterService: LoginRegisterService
) : ViewModel() {
    companion object {
        private const val TAG: String = "RegisterViewModel"
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun getCaptcha() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = true, captchaImage = null, shouldRefreshCaptcha = false)
            }
            try {
                delay(1000)
                val imageBitmap = ImageUtils.decodeImageString(
                    loginRegisterService.getCaptcha().data!!
                )
                _uiState.update { it.copy(isLoading = false, captchaImage = imageBitmap) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun register(uid: String, password: String, code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isRegistering = true, errorMessage = null) }
            try {
                loginRegisterService.register(uid, password, code)
                _uiState.update { it.copy(isRegistering = false, isUserRegistered = true) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    private fun catchHttpException(e: HttpException) {
        val response = RetrofitUtils.getErrorMessage(e)
        if (response == null) {
            catchException(e)
            return
        }

        Log.e(TAG, "$e: $response", e)
        val errorMessage = when (response.code) {
            ResponseCode.CAPTCHA_ERROR.code -> ErrorMessage.Toast("验证码错误！")
            ResponseCode.USER_EXIST_ERROR.code -> ErrorMessage.Dialog("账号已存在！")
            else -> ErrorMessage.Toast(response.message)
        }

        _uiState.update {
            it.copy(
                isRegistering = false,
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
                isRegistering = false,
                isLoading = false,
                errorMessage = ErrorMessage.Toast("${e.message}")
            )
        }
    }

    // UI 展示完瞬态消息后回调，清空该字段
    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}