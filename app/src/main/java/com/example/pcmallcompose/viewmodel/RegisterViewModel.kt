package com.example.pcmallcompose.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.service.LoginRegisterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val loginRegisterService: LoginRegisterService
) : ViewModel() {
    val captchaString: MutableState<String> = mutableStateOf("")//验证码

    fun getCaptcha() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            captchaString.value = loginRegisterService.getCaptcha().data!!
        }
    }
}