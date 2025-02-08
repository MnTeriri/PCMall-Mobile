package com.example.pcmallcompose.service

import com.example.pcmallcompose.model.User
import com.example.pcmallcompose.model.response.ResponseResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginRegisterService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("uid") uid: String,
        @Field("password") password: String,
        @Field("code") code: String
    ): ResponseResult<User>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("uid") uid: String,
        @Field("password") password: String,
        @Field("code") code: String
    ): ResponseResult<String>

    @GET("captcha.jpg")
    suspend fun getCaptcha(): ResponseResult<String>
}