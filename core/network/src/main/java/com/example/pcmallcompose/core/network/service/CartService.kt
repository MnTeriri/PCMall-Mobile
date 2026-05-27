package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.model.response.ResponseResult
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CartService {
    @FormUrlEncoded
    @POST("searchAllCart")
    suspend fun searchAllCart(
        @Field("uid") uid: String,
        @Field("currentPage") currentPage: Int,
        @Field("pageSize") pageSize: Int
    ): ResponseResult<List<Cart>>

    @FormUrlEncoded
    @POST("searchSelectCart")
    fun searchSelectCart(@Field("uid") uid: String): Observable<ResponseResult<List<Cart>>>

    @FormUrlEncoded
    @POST("getTotalCount")
    suspend fun getTotalCount(@Field("uid") uid: String): ResponseResult<Long>

    @FormUrlEncoded
    @POST("getNormalSelectedCount")
    suspend fun getNormalSelectedCount(@Field("uid") uid: String): ResponseResult<Long>

    @FormUrlEncoded
    @POST("getNormalTotalCount")
    suspend fun getNormalTotalCount(@Field("uid") uid: String): ResponseResult<Long>

    @FormUrlEncoded
    @POST("addCartCount")
    suspend fun addCartCount(@Field("id") id: Int): ResponseResult<String>

    @FormUrlEncoded
    @POST("subCartCount")
    suspend fun subCartCount(@Field("id") id: Int): ResponseResult<String>

    @FormUrlEncoded
    @POST("selectCart")
    suspend fun selectCart(
        @Field("id") id: Int,
        @Field("isSelect") isSelect: Int
    ): ResponseResult<String>

    @FormUrlEncoded
    @POST("selectAllCart")
    suspend fun selectAllCart(
        @Field("uid") uid: String,
        @Field("isSelect") isSelect: Int
    ): ResponseResult<String>

    @FormUrlEncoded
    @POST("addCart")
    suspend fun addCart(@Field("gid") goodsId: Int, @Field("uid") uid: String): ResponseResult<String>

    @FormUrlEncoded
    @POST("deleteCart")
    suspend fun deleteCart(@Field("id") id: Int): ResponseResult<String>
}