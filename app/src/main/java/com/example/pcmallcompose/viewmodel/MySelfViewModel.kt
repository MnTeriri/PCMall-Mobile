package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.network.service.OrderService
import com.example.pcmallcompose.core.network.utils.RetrofitUtils
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
import retrofit2.HttpException

data class MySelfUiState(
    val orderCounts: Map<Screen.Order.OrderTab, Long> = emptyMap(),
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class MySelfViewModel @Inject constructor(
    private val orderService: OrderService,
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
            try {
                val deferred = TABS_WITH_BADGE.map { tab ->
                    async {
                        tab to (orderService.getRecordsFiltered("", uid, tab.code).data ?: 0L)
                    }
                }
                deferred.awaitAll().forEach { (tab, count) ->
                    countMap[tab] = count
                }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
            _uiState.update { it.copy(orderCounts = countMap) }
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

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}