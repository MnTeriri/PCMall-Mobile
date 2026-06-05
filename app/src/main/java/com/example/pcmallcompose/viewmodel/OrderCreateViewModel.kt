package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.network.service.AddressService
import com.example.pcmallcompose.core.network.service.CartService
import com.example.pcmallcompose.core.network.service.OrderService
import com.example.pcmallcompose.core.network.utils.RetrofitUtils
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class OrderCreateUiState(
    val defaultAddress: Address? = null,
    val selectCarts: List<Cart> = emptyList(),
    val isOrderCreated: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class OrderCreateViewModel @Inject constructor(
    private val orderService: OrderService,
    private val cartService: CartService,
    private val addressService: AddressService,
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
            try {
                val data = addressService.searchDefaultAddress(uid).data
                _uiState.update { it.copy(defaultAddress = data) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun searchSelectCart() {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            _uiState.update { it.copy(selectCarts = emptyList()) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = cartService.searchSelectCart(uid).data ?: emptyList()
                _uiState.update { it.copy(selectCarts = data) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
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
            _uiState.update { it.copy(isOrderCreated = false) }
            try {
                orderService.createOrder(uid, aid)
                userSession.onCartChanged()
                _uiState.update { it.copy(isOrderCreated = true) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
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