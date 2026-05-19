package com.example.pcmallcompose.viewmodel

import com.example.pcmallcompose.core.model.response.ResponseCode

//UI错误信息
data class Message(val code: Int, val message: String){
    constructor(responseCode: ResponseCode) : this(responseCode.code, responseCode.message)
}
