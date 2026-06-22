package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.database.dao.CategoryDao
import com.example.pcmallcompose.core.database.entity.CategoryEntity
import com.example.pcmallcompose.core.model.Category
import com.example.pcmallcompose.core.network.service.CategoryService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val service: CategoryService,
    private val dao: CategoryDao
) {
    /** 本地缓存（同步返回） */
    suspend fun getCached(): List<Category> = dao.getAll().map { it.toCategory() }

    /** 远程拉取并更新缓存 */
    suspend fun refresh(): Result<List<Category>> = apiCall {
        val data = service.searchCategoryList().data ?: emptyList()
        dao.clearAll()
        dao.insertAll(data.map { CategoryEntity.fromCategory(it) })
        return@apiCall data
    }
}