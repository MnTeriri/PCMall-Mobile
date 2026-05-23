package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.core.model.ChatHistory
import com.example.pcmallcompose.core.model.ChatHistory.ChatHistoryType
import java.time.LocalDateTime

@Entity(tableName = "chat_history")
data class ChatHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "type") val type: ChatHistoryType,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime
) {
    fun toChatHistory(): ChatHistory = ChatHistory(
        type = type,
        content = content,
        createTime = createTime
    )

    companion object {
        fun fromChatHistory(chatHistory: ChatHistory): ChatHistoryEntity = ChatHistoryEntity(
            type = chatHistory.type,
            content = chatHistory.content,
            createTime = chatHistory.createTime
        )
    }
}