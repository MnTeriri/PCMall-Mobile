package com.example.pcmallcompose.ui

//UI错误信息
sealed class ErrorMessage {
    data class Dialog(val text: String) : ErrorMessage()
    data class Toast(val text: String) : ErrorMessage()
}