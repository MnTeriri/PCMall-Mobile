package com.example.pcmallcompose.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.core.database.entity.OrderEntity
import com.example.pcmallcompose.core.model.Order

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<OrderEntity>)

    // "全部" — 不加 status 过滤
    @Query("SELECT * FROM `order` WHERE uid=:uid ORDER BY id DESC")
    fun pagingSource(uid: String): PagingSource<Int, OrderEntity>

    // 按 status 过滤
    @Query("SELECT * FROM `order` WHERE uid=:uid AND status=:status ORDER BY id DESC")
    fun pagingSource(uid: String, status: Order.OrderState): PagingSource<Int, OrderEntity>

    @Query("DELETE FROM `order` WHERE uid=:uid")
    suspend fun clearAll(uid: String)
}