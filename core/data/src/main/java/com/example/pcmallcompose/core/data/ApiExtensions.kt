package com.example.pcmallcompose.core.data

import com.example.pcmallcompose.core.network.utils.RetrofitUtils
import retrofit2.HttpException

/**
 * 业务异常 — 携带后端返回的业务错误码。
 * ViewModel 根据 code 决定展示 Toast 还是 Dialog。
 */
class ApiException(
    val code: Int,
    override val message: String
) : Exception(message) {
    override fun toString(): String = "ApiException(code=$code, message=$message)"
}

/**
 * 统一的网络调用错误转换模板。
 * 异常转换规则：
 * - [retrofit2.HttpException] → 尝试从 errorBody 解析业务错误码，
 *   解析成功则返回 [ApiException]（携带 code + message），
 *   解析失败（errorBody 为空或格式异常）则以原始 [HttpException] 作为 failure。
 * - 其他异常 → 原样放入 [kotlin.Result.failure]。
 */
suspend inline fun <T> apiCall(crossinline block: suspend () -> T): Result<T> {
    try {
        return Result.success(block())
    } catch (e: HttpException) {
        val error = RetrofitUtils.getErrorMessage(e) ?: return Result.failure(e)
        return Result.failure(ApiException(error.code, error.message))
    } catch (e: Exception) {
        return Result.failure(e)
    }
}