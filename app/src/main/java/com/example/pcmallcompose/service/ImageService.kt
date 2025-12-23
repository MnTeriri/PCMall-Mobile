package com.example.pcmallcompose.service

import com.example.pcmallcompose.model.response.ResponseResult
import retrofit2.http.POST

interface ImageService {
    @POST("getADImageList")
    suspend fun getADImageList(): ResponseResult<List<String>>
}