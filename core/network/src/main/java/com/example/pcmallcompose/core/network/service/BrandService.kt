package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.Brand
import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BrandService {
    @FormUrlEncoded
    @POST("searchBrandByCid")
    suspend fun searchBrandByCid(@Field("cid") cid: Int): ResponseResult<List<Brand>>
}