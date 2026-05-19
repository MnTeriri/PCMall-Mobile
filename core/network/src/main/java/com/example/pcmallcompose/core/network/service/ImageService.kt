package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.http.POST

interface ImageService {
    @POST("getADImageList")
    suspend fun getADImageList(): ResponseResult<List<String>>
}