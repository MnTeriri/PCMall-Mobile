package com.example.pcmallcompose.core.network.utils

import com.alibaba.fastjson2.parseObject
import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.HttpException

object RetrofitUtils {
    @JvmStatic
    fun getErrorMessage(exception: HttpException): ResponseResult<String>? {
        val errorMessage = exception.response()?.errorBody()?.string()
        try {
            return errorMessage.parseObject<ResponseResult<String>>()
        } catch (e: Exception) {
            return null
        }
    }
}