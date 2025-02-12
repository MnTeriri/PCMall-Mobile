package com.example.pcmallcompose.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.response.ResponseCode
import com.example.pcmallcompose.service.LoginRegisterService
import com.example.pcmallcompose.utils.ImageUtils
import com.example.pcmallcompose.utils.RetrofitUtils
import com.example.pcmallcompose.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRegisterService: LoginRegisterService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    companion object {
        private const val TAG: String = "LoginViewModel"
    }

    val uiState = MutableSharedFlow<UiState>()
    val captchaImage: MutableState<ImageBitmap?> = mutableStateOf(null)//验证码

    fun getCaptcha() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            try {
                captchaImage.value =
                    ImageUtils.decodeImageString(loginRegisterService.getCaptcha().data!!)
            } catch (e: HttpException) {
                val errorResult = RetrofitUtils.getErrorMessage(e)
                if (errorResult != null) {
                    Log.e(TAG, "出现自定义错误：$errorResult")
                    uiState.emit(UiState.Error(errorResult.message!!, errorResult.code!!))
                } else {
                    Log.e(TAG, "出现错误：$e")
                    uiState.emit(UiState.Error("错误！"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "出现错误：$e")
                e.printStackTrace()
                uiState.emit(UiState.Error("错误！"))
            }
        }
    }

    fun login(uid: String, password: String, code: String, remember: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loginResponse = loginRegisterService.login(uid, password, code)
                Log.d(TAG, "登录成功：$loginResponse")
                val editor = sharedPreferences.edit()
                editor.putString("data", JSON.toJSONString(loginResponse.data))
                editor.putString("token", loginResponse.message)
                editor.putBoolean("remember", remember)
                editor.apply()
                uiState.emit(UiState.Success("登录成功！"))
            } catch (e: HttpException) {
                val errorResult = RetrofitUtils.getErrorMessage(e)
                if (errorResult != null) {
                    Log.e(TAG, "出现自定义错误：$errorResult")
                    uiState.emit(UiState.Error(errorResult.message!!, errorResult.code!!))
                } else {
                    Log.e(TAG, "出现错误：$e")
                    uiState.emit(UiState.Error("错误！"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "出现错误：$e")
                e.printStackTrace()
                uiState.emit(UiState.Error("错误！"))
            }
        }
    }
}