package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.http.GET

interface ImageService {
    @GET("getADImageList")
    suspend fun getADImageList(): ResponseResult<List<String>>
}