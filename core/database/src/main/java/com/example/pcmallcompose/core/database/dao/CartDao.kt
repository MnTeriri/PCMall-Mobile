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

    @Query("UPDATE cart SET count=count+1 WHERE id=:id")
    suspend fun addCartCount(id: Int)

    @Query("UPDATE cart SET count=count-1 WHERE id=:id")
    suspend fun subCartCount(id: Int)

    @Query("UPDATE cart SET is_select=:isSelect WHERE id=:id")
    suspend fun updateSelectCart(id: Int, isSelect: Int)

    @Query("DELETE FROM cart WHERE id=:id")
    suspend fun deleteById(id: Int)
}