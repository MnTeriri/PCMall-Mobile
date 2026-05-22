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
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
)

@HiltViewModel
class AiChatViewModel @Inject constructor(
    val chatHistoryDao: ChatHistoryDao
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
            _uiState.update { it.copy(isChatting = true) }

            var chatHistory = ChatHistory(ChatHistory.ChatHistoryType.USER, message, LocalDateTime.now())
            chatHistoryDao.insertOrReplace(ChatHistoryEntity.fromChatHistory(chatHistory))

            delay(3000)

            chatHistory =
                ChatHistory(ChatHistory.ChatHistoryType.AI, "${message}${LocalDateTime.now()}", LocalDateTime.now())
            chatHistoryDao.insertOrReplace(ChatHistoryEntity.fromChatHistory(chatHistory))

            _uiState.update { it.copy(isChatting = false) }
        }
    }
}