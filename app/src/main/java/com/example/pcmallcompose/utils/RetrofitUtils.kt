package com.example.pcmallcompose.utils

import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.response.ResponseResult
import retrofit2.HttpException

class RetrofitUtils {

    companion object {
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

}