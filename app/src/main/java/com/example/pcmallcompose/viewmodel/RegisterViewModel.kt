package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

sealed class RegisterUiEvent {
    data class Success(val message: String = "") : RegisterUiEvent()//成功
    data class Error(val message: String = "") : RegisterUiEvent()//未知错误
    data object CaptchaError : RegisterUiEvent()//验证码错误
    data object UserExistError : RegisterUiEvent()//用户已存在
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val loginRegisterService: LoginRegisterService
) : ViewModel() {
    companion object {
        private const val TAG: String = "RegisterViewModel"
    }

    private val _uiEvent = MutableSharedFlow<RegisterUiEvent>()
    val uiEvent: SharedFlow<RegisterUiEvent> = _uiEvent.asSharedFlow()

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
                    _uiEvent.emit(RegisterUiEvent.Error("错误！"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "出现错误：$e")
                e.printStackTrace()
                _uiEvent.emit(RegisterUiEvent.Error("错误！"))
            }
        }
    }

    fun getCaptcha() {
        launchSafe({
            delay(1000)
            captchaImage.value = ImageUtils.decodeImageString(loginRegisterService.getCaptcha().data!!)
        })
    }

    fun register(uid: String, password: String, code: String) {
        launchSafe({
            loginRegisterService.register(uid, password, code)
            _uiEvent.emit(RegisterUiEvent.Success("注册成功！"))
        }) { errorResult ->
            when (errorResult.code) {
                ResponseCode.CAPTCHA_ERROR.code -> {
                    _uiEvent.emit(RegisterUiEvent.CaptchaError)
                }

                ResponseCode.USER_EXIST_ERROR.code -> {
                    _uiEvent.emit(RegisterUiEvent.UserExistError)
                }

                else -> {
                    _uiEvent.emit(RegisterUiEvent.Error(errorResult.message!!))
                }
            }
        }
    }
}