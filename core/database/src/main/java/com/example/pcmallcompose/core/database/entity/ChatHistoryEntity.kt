package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.core.model.ChatHistory
import com.example.pcmallcompose.core.model.ChatHistory.ChatHistoryType
import com.example.pcmallcompose.core.model.ChatHistory.ChatStatus
import com.example.pcmallcompose.core.model.Goods
import java.time.LocalDateTime

@Entity(tableName = "chat_history")
data class ChatHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "type") val type: ChatHistoryType,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "recommends") var recommends: List<Goods> = emptyList(),
    @ColumnInfo(name = "chat_status") val chatStatus: ChatStatus,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime
) {
    fun toChatHistory(): ChatHistory = ChatHistory(
        type = type,
        content = content,
        recommends = recommends,
        chatStatus = chatStatus,
        createTime = createTime
    )

    companion object {
        @JvmStatic
        fun fromChatHistory(chatHistory: ChatHistory, uid: String): ChatHistoryEntity =
            ChatHistoryEntity(
                type = chatHistory.type,
                uid = uid,
                content = chatHistory.content,
                recommends = chatHistory.recommends,
                chatStatus = chatHistory.chatStatus,
                createTime = chatHistory.createTime
            )
    }
}