package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.network.service.GoodsService
import com.example.pcmallcompose.core.network.service.ImageService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class GoodsRepository @Inject constructor(
    private val goodsService: GoodsService,
    private val imageService: ImageService
) {
    suspend fun searchGoodsList(
        searchValue: String, currentPage: Int, pageSize: Int
    ): Result<List<Goods>> = apiCall {
        goodsService.searchGoodsList(searchValue, currentPage, pageSize).data ?: emptyList()
    }

    suspend fun getTotalCount(searchValue: String): Result<Long> = apiCall {
        goodsService.getTotalCount(searchValue).data ?: 0L
    }

    suspend fun getADImageList(): Result<List<String>> = apiCall {
        imageService.getADImageList().data ?: emptyList()
    }
}