package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoodsService {
    @FormUrlEncoded
    @POST("searchGoodsByValue")
    suspend fun searchGoodsList(
        @Field("searchValue") searchValue: String,
        @Field("currentPage") currentPage: Int,
        @Field("pageSize") pageSize: Int
    ): ResponseResult<List<Goods>>

    @FormUrlEncoded
    @POST("getTotalCountByValue")
    suspend fun getTotalCount(@Field("searchValue") searchValue: String): ResponseResult<Long>
}