package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.database.dao.BrandDao
import com.example.pcmallcompose.core.database.entity.BrandEntity
import com.example.pcmallcompose.core.model.Brand
import com.example.pcmallcompose.core.network.service.BrandService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class BrandRepository @Inject constructor(
    private val service: BrandService,
    private val dao: BrandDao
) {
    suspend fun getCached(cid: Int): List<Brand> = dao.getAll(cid).map { it.toBrand() }

    suspend fun refresh(cid: Int): Result<List<Brand>> = apiCall {
        val data = service.searchBrandByCid(cid).data ?: emptyList()
        dao.clearAll(cid)
        dao.insertAll(data.map { BrandEntity.fromBrand(it, cid) })
        return@apiCall data
    }
}