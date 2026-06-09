package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.repository.OrderRepository
import com.example.pcmallcompose.ui.ErrorMessage
import com.example.pcmallcompose.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MySelfUiState(
    val orderCounts: Map<Screen.Order.OrderTab, Long> = emptyMap(),
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class MySelfViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        private const val TAG = "MySelfViewModel"

        // 在订单卡片中展示 Badge 的分类列表
        private val TABS_WITH_BADGE = listOf(
            Screen.Order.OrderTab.PENDING_PAYMENT,
            Screen.Order.OrderTab.PENDING_SHIPMENT,
            Screen.Order.OrderTab.PENDING_RECEIPT,
            Screen.Order.OrderTab.SUCCESS,
            Screen.Order.OrderTab.RETURNING,
        )
    }

    private val _uiState = MutableStateFlow(MySelfUiState())
    val uiState: StateFlow<MySelfUiState> = _uiState.asStateFlow()

    fun fetchOrderCounts() {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            _uiState.update { it.copy(orderCounts = emptyMap()) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val countMap = mutableMapOf<Screen.Order.OrderTab, Long>()
            val deferred = TABS_WITH_BADGE.map { tab ->
                async {
                    tab to orderRepository.getRecordsFiltered("", uid, tab.code).getOrDefault(0L)
                }
            }
            deferred.awaitAll().forEach { (tab, count) -> countMap[tab] = count }
            _uiState.update { it.copy(orderCounts = countMap) }
        }
    }

    private fun handleError(e: Throwable) {
        Log.e(TAG, e.toString(), e)
        val msg = if (e is ApiException) {
            Log.w(TAG, e.toString(), e)
            e.message
        } else {
            Log.e(TAG, e.toString(), e)
            e.message.orEmpty()
        }
        _uiState.update { it.copy(errorMessage = ErrorMessage.Toast(msg)) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}