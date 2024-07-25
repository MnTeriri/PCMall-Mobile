package com.example.pcmallcompose.service

import com.example.pcmallcompose.model.response.ResponseResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoodsService {
    @FormUrlEncoded
    @POST("searchGoodsList")
    suspend fun searchGoodsList(
        @Field("searchValue") searchValue: String?,
        @Field("currentPage") currentPage: Int?,
        @Field("pageSize") pageSize: Int?
    ): ResponseResult<Map<String, String>>
}