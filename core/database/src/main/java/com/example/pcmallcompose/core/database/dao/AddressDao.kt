package com.example.pcmallcompose.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.core.database.entity.AddressEntity

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(addresses: List<AddressEntity>)

    @Query("SELECT * FROM address WHERE uid=:uid ORDER BY id DESC")
    fun pagingSource(uid: String): PagingSource<Int, AddressEntity>

    @Query("DELETE FROM address WHERE uid=:uid")
    suspend fun clearAll(uid: String)
}