package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OrderService {
    @FormUrlEncoded
    @POST("searchOrderList")
    suspend fun searchOrderList(
        @Field("searchValue") searchValue: String,
        @Field("uid") uid: String,
        @Field("type") type: Int,
        @Field("currentPage") currentPage: Int,
        @Field("pageSize") pageSize: Int
    ): ResponseResult<List<Order>>

    @FormUrlEncoded
    @POST("getRecordsFiltered")
    suspend fun getRecordsFiltered(
        @Field("searchValue") searchValue: String,
        @Field("uid") uid: String,
        @Field("type") type: Int
    ): ResponseResult<Long>

    @FormUrlEncoded
    @POST("createOrder")
    suspend fun createOrder(
        @Field("uid") uid: String,
        @Field("aid") aid: Int
    ): ResponseResult<String>

    @FormUrlEncoded
    @POST("payOrder")
    suspend fun payOrder(@Field("oid") oid: String): ResponseResult<String>

    @FormUrlEncoded
    @POST("finishOrder")
    suspend fun finishOrder(@Field("oid") oid: String): ResponseResult<String>

    @FormUrlEncoded
    @POST("cancelOrder")
    suspend fun cancelOrder(@Field("oid") oid: String): ResponseResult<String>

    @FormUrlEncoded
    @POST("refundOrder")
    suspend fun refundOrder(@Field("oid") oid: String): ResponseResult<String>
}