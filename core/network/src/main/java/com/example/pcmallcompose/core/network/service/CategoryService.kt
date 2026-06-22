package com.example.pcmallcompose.core.network.service

import com.example.pcmallcompose.core.model.Category
import com.example.pcmallcompose.core.model.response.ResponseResult
import retrofit2.http.POST

interface CategoryService {
    @POST("searchCategoryList")
    suspend fun searchCategoryList(): ResponseResult<List<Category>>
}