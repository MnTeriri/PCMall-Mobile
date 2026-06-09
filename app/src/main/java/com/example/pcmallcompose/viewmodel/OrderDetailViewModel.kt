package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.repository.OrderRepository
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderDetailUiState(
    val isLoading: Boolean = false, //用于控制Button的启用
    val isSuccess: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "OrderDetailViewModel"
    }

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

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
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { handleError(it) }
        }
    }

    fun cancelOrder(oid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, isSuccess = false) }
            orderRepository.cancelOrder(oid)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { handleError(it) }
        }
    }

    fun refundOrder(oid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, isSuccess = false) }
            orderRepository.refundOrder(oid)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
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