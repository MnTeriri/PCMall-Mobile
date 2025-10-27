package com.example.pcmallcompose.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.response.ResponseCode
import com.example.pcmallcompose.model.response.ResponseResult
import com.example.pcmallcompose.service.LoginRegisterService
import com.example.pcmallcompose.utils.ImageUtils
import com.example.pcmallcompose.utils.RetrofitUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

sealed class LoginUiEvent {
    data class Success(val message: String = "") : LoginUiEvent()//成功
    data class Error(val message: String = "") : LoginUiEvent()//未知错误
    data object CaptchaError : LoginUiEvent()//验证码错误
    data object AccountError : LoginUiEvent()//账号或密码错误
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRegisterService: LoginRegisterService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    companion object {
        private const val TAG: String = "LoginViewModel"
    }

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    val captchaImage: MutableState<ImageBitmap?> = mutableStateOf(null)//验证码

    fun launchSafe(
        block: suspend () -> Unit,
        handleCustomError: suspend (errorResult: ResponseResult<String>) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { block() }
            } catch (e: UnknownHostException) { // 网络异常
                Log.e(TAG, "网络异常：$e")
            } catch (e: HttpException) {//HTTP异常
                val errorResult = RetrofitUtils.getErrorMessage(e)
                if (errorResult != null) {
                    handleCustomError(errorResult)
                } else {
                    Log.e(TAG, "出现HTTP错误：$e")
                    _uiEvent.emit(LoginUiEvent.Error("错误！"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "出现错误：$e")
                e.printStackTrace()
                _uiEvent.emit(LoginUiEvent.Error("错误！"))
            }
        }
    }

    fun getCaptcha() {
        launchSafe({
            delay(1000)
            captchaImage.value = ImageUtils.decodeImageString(loginRegisterService.getCaptcha().data!!)
        })
    }

    fun login(uid: String, password: String, code: String, remember: Boolean) {
        launchSafe({
            val loginResponse = loginRegisterService.login(uid, password, code)
            Log.d(TAG, "登录成功：$loginResponse")
            sharedPreferences.edit {
                putString("data", JSON.toJSONString(loginResponse.data))
                putString("token", loginResponse.message)
                putBoolean("remember", remember)
            }
            _uiEvent.emit(LoginUiEvent.Success("登录成功！"))
        }) { errorResult ->
            when (errorResult.code) {
                ResponseCode.CAPTCHA_ERROR.code -> {
                    _uiEvent.emit(LoginUiEvent.CaptchaError)
                }

                ResponseCode.ACCOUNT_ERROR.code -> {
                    _uiEvent.emit(LoginUiEvent.AccountError)
                }

                else -> {
                    _uiEvent.emit(LoginUiEvent.Error(errorResult.message!!))
                }
            }
        }
    }
}