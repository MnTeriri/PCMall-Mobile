package com.example.pcmallcompose.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.core.database.entity.CartEntity

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<CartEntity>)

    @Query("SELECT * FROM cart WHERE uid=:uid ORDER BY id DESC")
    fun pagingSource(uid: String): PagingSource<Int, CartEntity>

    @Query("DELETE FROM cart WHERE uid=:uid")
    suspend fun clearAll(uid: String)
}