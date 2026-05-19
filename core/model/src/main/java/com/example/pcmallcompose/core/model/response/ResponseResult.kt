package com.example.pcmallcompose.core.model.response

data class ResponseResult<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)