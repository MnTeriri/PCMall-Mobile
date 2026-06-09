package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.model.User
import com.example.pcmallcompose.core.network.service.LoginRegisterService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val service: LoginRegisterService
) {
    suspend fun login(uid: String, password: String, code: String): Result<Pair<User?, String>> = apiCall {
        val result = service.login(uid, password, code)
        Pair(result.data, result.message)
    }

    suspend fun register(uid: String, password: String, code: String): Result<Unit> = apiCall {
        service.register(uid, password, code).data
    }

    suspend fun getCaptcha(): Result<String?> = apiCall {
        service.getCaptcha().data
    }
}