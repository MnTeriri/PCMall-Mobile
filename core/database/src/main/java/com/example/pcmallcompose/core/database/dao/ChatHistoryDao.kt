package com.example.pcmallcompose.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcmallcompose.core.database.entity.ChatHistoryEntity

@Dao
interface ChatHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(chatHistoryEntity: ChatHistoryEntity): Long

    @Query("SELECT * FROM chat_history ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, ChatHistoryEntity>
}