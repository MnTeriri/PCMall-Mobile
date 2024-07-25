package com.example.pcmallcompose.model.response


data class ResponseResult<T>(
    var code: Int? = null,
    var message: String? = null,
    var data: T? = null
) {
    companion object {
        @JvmStatic
        fun <T> ok(): ResponseResult<T> {
            val result: ResponseResult<T> = ResponseResult()
            result.code = ResponseCode.OK.code
            result.message = ResponseCode.OK.message
            result.data = null
            return result
        }
    }
}