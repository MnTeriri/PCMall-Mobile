package com.example.pcmallcompose.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.model.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_key WHERE table_name=:tableName AND label = :query")
    suspend fun remoteKeyByQuery(tableName: String, query: String): RemoteKey

    @Query("DELETE FROM remote_key WHERE table_name=:tableName AND label = :query")
    suspend fun deleteByQuery(tableName: String, query: String)
}