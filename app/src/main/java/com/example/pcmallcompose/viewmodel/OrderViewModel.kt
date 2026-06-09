package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.paging.OrderRemoteMediator
import com.example.pcmallcompose.core.data.repository.OrderRepository
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.dao.OrderDao
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderUiState(
    val isLoading: Boolean = false, // 用于控制Button的启用
    val isSuccess: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val orderDao: OrderDao,
    private val orderRepository: OrderRepository,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        private const val TAG = "OrderViewModel"
    }

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    // ── type 缓存：key = "type"，uid 变化时整体清空 ──
    private var cachedKey: String = ""
    private val typePagerCache = mutableMapOf<Int, Flow<PagingData<Order>>>()

    // tab 切换时调用（同一 uid + type 只创建一次 Pager ）
    fun getOrderPagingData(type: Int): Flow<PagingData<Order>> {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            return flowOf(PagingData.empty())
        }

        if (uid != cachedKey) {
            cachedKey = uid
            typePagerCache.clear()
        }

        return typePagerCache.getOrPut(type) {
            createOrderPager("", uid, type)
        }
    }

    // 搜索值变化时调用 — 每次都返回全新 Pager，不命中 type 缓存
    fun getSearchOrderPagingData(searchValue: String, type: Int): Flow<PagingData<Order>> {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            return flowOf(PagingData.empty())
        }
        return createOrderPager(searchValue, uid, type)
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun createOrderPager(searchValue: String, uid: String, type: Int): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = OrderRemoteMediator(searchValue, uid, type, database, orderRepository)
        ) {
            if (type == -1) {
                orderDao.pagingSource(uid)
            } else {
                orderDao.pagingSource(uid, Order.OrderState.fromCode(type))
            }
        }.flow.cachedIn(viewModelScope).map { pagingData -> pagingData.map { it.toOrder() } }
    }

    fun payOrder(oid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, isSuccess = false) }
            orderRepository.payOrder(oid)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { handleError(it) }
        }
    }

    fun finishOrder(oid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, isSuccess = false) }
            orderRepository.finishOrder(oid)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true,) } }
                .onFailure { handleError(it) }
        }
    }

    fun cancelOrder(oid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, isSuccess = false) }
            orderRepository.cancelOrder(oid)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true,) } }
                .onFailure { handleError(it) }
        }
    }

    fun refundOrder(oid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, isSuccess = false) }
            orderRepository.refundOrder(oid)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true,) } }
                .onFailure { handleError(it) }
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

    fun successConsumed() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}