package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.repository.AddressRepository
import com.example.pcmallcompose.core.data.repository.CartRepository
import com.example.pcmallcompose.core.data.repository.OrderRepository
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderCreateUiState(
    val defaultAddress: Address? = null,
    val selectCarts: List<Cart> = emptyList(),
    val isOrderCreated: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class OrderCreateViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val addressRepository: AddressRepository,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        private const val TAG = "OrderCreateViewModel"
    }

    private val _uiState = MutableStateFlow(OrderCreateUiState())
    val uiState: StateFlow<OrderCreateUiState> = _uiState.asStateFlow()

    fun searchDefaultAddress() {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            _uiState.update { it.copy(defaultAddress = null) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            addressRepository.searchDefaultAddress(uid)
                .onSuccess { data -> _uiState.update { it.copy(defaultAddress = data) } }
                .onFailure { handleError(it) }
        }
    }

    fun searchSelectCart() {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            _uiState.update { it.copy(selectCarts = emptyList()) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.searchSelectCart(uid)
                .onSuccess { data -> _uiState.update { it.copy(selectCarts = data) } }
                .onFailure { handleError(it) }
        }
    }

    fun createOrder() {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            return
        }
        val aid = uiState.value.defaultAddress?.id
        if (aid == null) {
            _uiState.update { it.copy(errorMessage = ErrorMessage.Dialog("请选择收货地址")) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.createOrder(uid, aid)
                .onSuccess {
                    userSession.onCartChanged()
                    _uiState.update { it.copy(isOrderCreated = true) }
                }
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

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}