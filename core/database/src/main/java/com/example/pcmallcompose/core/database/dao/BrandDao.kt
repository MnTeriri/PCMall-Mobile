package com.example.pcmallcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.core.database.entity.BrandEntity

@Dao
interface BrandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<BrandEntity>)

    @Query("SELECT * FROM brand WHERE cid=:cid")
    suspend fun getAll(cid: Int): List<BrandEntity>

    @Query("DELETE FROM brand WHERE cid=:cid")
    suspend fun clearAll(cid: Int)
}