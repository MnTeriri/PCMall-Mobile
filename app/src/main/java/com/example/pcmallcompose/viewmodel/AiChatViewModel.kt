package com.example.pcmallcompose.viewmodel

import android.util.Log
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
import com.example.pcmallcompose.core.model.ChatHistory.ChatStatus
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.model.ai.AiChatEvent
import com.example.pcmallcompose.core.model.ai.AiChatEvent.AiChatEventType.GOODS
import com.example.pcmallcompose.core.model.ai.AiChatEvent.AiChatEventType.TEXT
import com.example.pcmallcompose.core.network.sse.AiChatSseClient
import com.example.pcmallcompose.core.network.utils.RetrofitUtils
import com.example.pcmallcompose.ui.ErrorMessage
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
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
import retrofit2.HttpException
import java.time.LocalDateTime

data class AiChatUiState(
    val isChatting: Boolean = false,//是否正在聊天
    val streamingContent: String = "",
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val chatHistoryDao: ChatHistoryDao,
    private val aiChatSseClient: AiChatSseClient,
    private val objectMapper: ObjectMapper
) : ViewModel() {
    companion object {
        private const val TAG = "AiChatViewModel"
    }

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    val chatHistoryPagingFlow: Flow<PagingData<ChatHistory>> = Pager(
        config = PagingConfig(pageSize = 10),
    ) {
        chatHistoryDao.pagingSource()
    }.flow.cachedIn(viewModelScope).map { pagingData ->
        pagingData.map { it.toChatHistory() }
    }

    private fun handleAiChatEvent(
        builder: StringBuilder,
        chatHistoryEntity: ChatHistoryEntity,
        aiChatEvent: AiChatEvent
    ) {
        when (aiChatEvent.type) {
            GOODS -> {
                chatHistoryEntity.recommends = objectMapper.convertValue(
                    aiChatEvent.data,
                    object : TypeReference<List<Goods>>() {})
            }

            TEXT -> {
                builder.append(aiChatEvent.data)
                _uiState.update { it.copy(streamingContent = builder.toString()) }
            }

            else -> {

            }
        }
    }

    fun chat(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isChatting = true, streamingContent = "", errorMessage = null) }

            // 1. 插入用户消息
            val userHistoryEntity = ChatHistoryEntity(
                type = ChatHistoryType.USER,
                content = message,
                chatStatus = ChatStatus.FINISH,
                createTime = LocalDateTime.now()
            )
            chatHistoryDao.insertOrReplace(userHistoryEntity)

            // 2. 插入空占位行，拿到自增 ID
            val placeholderEntity = ChatHistoryEntity(
                type = ChatHistoryType.AI,
                content = "",
                chatStatus = ChatStatus.CHATTING,
                createTime = LocalDateTime.now()
            )
            val placeholderId = chatHistoryDao.insertOrReplace(placeholderEntity)

            val builder = StringBuilder()

            try {
                aiChatSseClient.chat("000000000", "123123", message).collect {
                    handleAiChatEvent(builder, placeholderEntity, it)
                }
                // 3. 流结束，保存完整 AI 回复到 Room
                val content = builder.toString()
                if (content.isNotBlank()) {
                    chatHistoryDao.insertOrReplace(
                        placeholderEntity.copy(
                            id = placeholderId,
                            content = builder.toString(),
                            chatStatus = ChatStatus.FINISH,
                            createTime = LocalDateTime.now()
                        )
                    )
                }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                Log.e(TAG, "$e: ${e.message}", e)
                chatHistoryDao.insertOrReplace(
                    placeholderEntity.copy(
                        id = placeholderId,
                        chatStatus = ChatStatus.ERROR,
                        createTime = LocalDateTime.now()
                    )
                )
                catchException(e)
            }

            _uiState.update { it.copy(isChatting = false) }
        }
    }

    private fun catchHttpException(e: HttpException) {
        val response = RetrofitUtils.getErrorMessage(e)
        if (response == null) {
            catchException(e)
            return
        }
        Log.w(TAG, "$e: $response", e)
        _uiState.update { it.copy(errorMessage = ErrorMessage.Toast(response.message)) }
    }

    private fun catchException(e: Exception) {
        Log.e(TAG, e.toString(), e)
        _uiState.update { it.copy(errorMessage = ErrorMessage.Toast("${e.message}")) }
    }

    // UI 展示完瞬态消息后回调，清空该字段
    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}