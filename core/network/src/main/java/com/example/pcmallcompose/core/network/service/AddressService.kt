package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.model.dto.AddressDTO
import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AddressService {
    @FormUrlEncoded
    @POST("searchAddressList")
    suspend fun searchAddressList(@Field("uid") uid: String): ResponseResult<List<Address>>

    @FormUrlEncoded
    @POST("searchDefaultAddress")
    suspend fun searchDefaultAddress(@Field("uid") uid: String): ResponseResult<Address>

    @POST("addAddress")
    suspend fun addAddress(@Body address: AddressDTO): ResponseResult<String>

    @POST("updateAddress")
    suspend fun updateAddress(@Body address: AddressDTO): ResponseResult<String>

    @FormUrlEncoded
    @POST("deleteAddress")
    suspend fun deleteAddress(@Field("id") id: Int): ResponseResult<String>
}