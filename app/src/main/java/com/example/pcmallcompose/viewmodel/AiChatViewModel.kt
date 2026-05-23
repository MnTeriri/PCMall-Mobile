package com.example.pcmallcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pcmallcompose.core.database.dao.ChatHistoryDao
import com.example.pcmallcompose.core.database.entity.ChatHistoryEntity
import com.example.pcmallcompose.core.model.ChatHistory
import com.example.pcmallcompose.core.model.ChatHistory.ChatHistoryType
import com.example.pcmallcompose.core.model.ai.AiChatEvent.AiChatEventType.GOODS
import com.example.pcmallcompose.core.model.ai.AiChatEvent.AiChatEventType.TEXT
import com.example.pcmallcompose.core.network.sse.AiChatSseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class AiChatUiState(
    val isChatting: Boolean = false,//是否正在聊天
    val streamingContent: String = "",
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao,
    private val aiChatSseClient: AiChatSseClient
) : ViewModel() {
    companion object {
        const val TAG = "AiChatViewModel"
    }

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun getChatHistoryPagingData(): Flow<PagingData<ChatHistory>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
        ) {
            chatHistoryDao.pagingSource()
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { it.toChatHistory() }
        }
    }

    fun chat(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isChatting = true, streamingContent = "", errorMessage = null) }

            // 1. 插入用户消息
            val userHistory = ChatHistory(ChatHistoryType.USER, message, LocalDateTime.now())
            chatHistoryDao.insertOrReplace(ChatHistoryEntity.fromChatHistory(userHistory))

            // 2. 插入空占位行，拿到自增 ID
            val placeholder = ChatHistory(ChatHistoryType.AI, "", LocalDateTime.now())
            val placeholderEntity = ChatHistoryEntity.fromChatHistory(placeholder)
            val placeholderId = chatHistoryDao.insertOrReplace(placeholderEntity)

            val builder = StringBuilder()
            aiChatSseClient.chat("000000000", "123123", message).collect { result ->
                when (result.type) {
                    GOODS -> {
                    }
                    TEXT -> {
                        builder.append(result.data)
                        _uiState.update { it.copy(streamingContent = builder.toString()) }
                    }
                    else -> {

                    }
                }
            }

            // 3. 流结束，保存完整 AI 回复到 Room
            val content = builder.toString()
            if (content.isNotBlank()) {
                chatHistoryDao.insertOrReplace(
                    placeholderEntity.copy(
                        id = placeholderId,
                        content = builder.toString(),
                        createTime = LocalDateTime.now()
                    )
                )
            }

            _uiState.update { it.copy(isChatting = false) }
        }
    }
}