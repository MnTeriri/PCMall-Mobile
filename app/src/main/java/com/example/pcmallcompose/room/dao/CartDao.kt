package com.example.pcmallcompose.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.model.Cart

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Cart>)

    @Query("SELECT * FROM cart ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, Cart>

    @Query("DELETE FROM cart")
    suspend fun clearAll()
}