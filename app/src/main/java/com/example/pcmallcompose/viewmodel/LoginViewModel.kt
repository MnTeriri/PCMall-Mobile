package com.example.pcmallcompose.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.response.ResponseCode
import com.example.pcmallcompose.service.LoginRegisterService
import com.example.pcmallcompose.utils.ImageUtils
import com.example.pcmallcompose.utils.RetrofitUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class CaptchaUiState(
    val isLoading: Boolean = true,
    val captchaImage: ImageBitmap? = null,
)

data class LoginUiState(
    val captchaUiState: CaptchaUiState,
    val isLoginSuccess: Boolean = false,
    val isError: Boolean = false,
    val message: Message? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRegisterService: LoginRegisterService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    companion object {
        private const val TAG: String = "LoginViewModel"
    }

    var uiState by mutableStateOf(LoginUiState(captchaUiState = CaptchaUiState()))
        private set

    fun getCaptcha() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uiState = uiState.copy(
                    isError = false,
                    message = null,
                    captchaUiState = CaptchaUiState(true, null)
                )
                delay(1000)
                val imageBitmap = ImageUtils.decodeImageString(loginRegisterService.getCaptcha().data!!)
                uiState = uiState.copy(
                    captchaUiState = CaptchaUiState(false, imageBitmap),
                    message = Message(ResponseCode.OK)
                )
            } catch (e: HttpException) {//HTTP异常
                val errorResult = RetrofitUtils.getErrorMessage(e)
                if (errorResult != null) {
                    uiState = uiState.copy(
                        isError = true,
                        message = Message(errorResult.code!!, errorResult.message!!)
                    )
                } else {
                    Log.e(TAG, "出现HTTP错误：$e")
                    uiState = uiState.copy(
                        isError = true,
                        message = Message(ResponseCode.ERROR)
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "出现错误：$e")
                e.printStackTrace()
                uiState = uiState.copy(isError = true, message = Message(ResponseCode.ERROR))
            }
        }
    }

    fun login(uid: String, password: String, code: String, remember: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uiState = uiState.copy(
                    isLoginSuccess = false,
                    isError = false,
                    message = null,
                )
                val loginResponse = loginRegisterService.login(uid, password, code)
                Log.d(TAG, "登录成功：$loginResponse")
                sharedPreferences.edit {
                    putString("data", JSON.toJSONString(loginResponse.data))
                    putString("token", loginResponse.message)
                    putBoolean("remember", remember)
                }
                uiState = uiState.copy(
                    isLoginSuccess = true,
                    message = Message(ResponseCode.OK)
                )
            } catch (e: HttpException) {//HTTP异常
                val errorResult = RetrofitUtils.getErrorMessage(e)
                if (errorResult != null) {
                    uiState = uiState.copy(
                        isError = true,
                        message = Message(errorResult.code!!, errorResult.message!!)
                    )
                } else {
                    Log.e(TAG, "出现HTTP错误：$e")
                    uiState = uiState.copy(
                        isError = true,
                        message = Message(ResponseCode.ERROR)
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "出现错误：$e")
                e.printStackTrace()
                uiState = uiState.copy(isError = true, message = Message(ResponseCode.ERROR))
            }
        }
    }
}