package com.example.pcmallcompose.utils

import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.HttpException

object RetrofitUtils {
    @JvmStatic
    fun getErrorMessage(exception: HttpException): ResponseResult<String>? {
        val errorMessage = exception.response()?.errorBody()?.string()
        if (errorMessage != null) {
            return JSON.parseObject(
                errorMessage,
                ResponseResult::class.java
            ) as ResponseResult<String>?
        }
        return null
    }
}