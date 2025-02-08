package com.example.pcmallcompose.model.response

data class ResponseResult<T>(
    var code: Int? = null,
    var message: String? = null,
    var data: T? = null
)