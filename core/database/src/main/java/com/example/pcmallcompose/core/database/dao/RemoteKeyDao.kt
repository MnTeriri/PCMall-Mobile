package com.example.pcmallcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.core.database.entity.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_key WHERE table_name=:tableName AND label = :label")
    suspend fun remoteKeyByQuery(tableName: String, label: String): RemoteKey

    @Query("DELETE FROM remote_key WHERE table_name=:tableName AND label = :label")
    suspend fun deleteByQuery(tableName: String, label: String)
}