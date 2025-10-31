package com.example.pcmallcompose.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.room.entity.GoodsEntity

@Dao
interface GoodsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goodsList: List<GoodsEntity>)

    @Query("SELECT * FROM goods WHERE label=:label AND search_value=:searchValue")
    fun pagingSource(label: String, searchValue: String): PagingSource<Int, GoodsEntity>

    @Query("DELETE FROM goods WHERE label=:label AND search_value=:searchValue")
    suspend fun clearAll(label: String, searchValue: String)
}