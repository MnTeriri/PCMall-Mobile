package com.example.pcmallcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.core.database.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CategoryEntity>)

    @Query("SELECT * FROM category")
    suspend fun getAll(): List<CategoryEntity>

    @Query("DELETE FROM category")
    suspend fun clearAll()
}