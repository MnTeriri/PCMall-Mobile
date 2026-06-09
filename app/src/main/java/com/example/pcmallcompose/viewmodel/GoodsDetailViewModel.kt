package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.repository.CartRepository
import com.example.pcmallcompose.core.model.response.ResponseCode
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoodsDetailUiState(
    val isAddingToCart: Boolean = false,        // 加入购物车请求中
    val isAddedToCart: Boolean = false,         // 加入成功，触发弹窗
    val errorMessage: ErrorMessage? = null,     // 瞬态错误
)

@HiltViewModel
class GoodsDetailViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        private const val TAG = "GoodsDetailViewModel"
    }

    private val _uiState = MutableStateFlow(GoodsDetailUiState())
    val uiState: StateFlow<GoodsDetailUiState> = _uiState.asStateFlow()

    fun addToCart(goodsId: Int, uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isAddingToCart = true, isAddedToCart = false, errorMessage = null) }
            cartRepository.addCart(goodsId, uid)
                .onSuccess {
                    userSession.onCartChanged()
                    _uiState.update { it.copy(isAddingToCart = false, isAddedToCart = true) }
                }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(e: Throwable) {
        when (e) {
            is ApiException -> {
                Log.w(TAG, e.toString(), e)
                val errorMessage = when (e.code) {
                    ResponseCode.CART_GOODS_ERROR.code -> ErrorMessage.Dialog("购物车商品状态异常")
                    ResponseCode.GOODS_NOT_ENOUGH_ERROR.code -> ErrorMessage.Dialog("商品库存不足")
                    ResponseCode.GOODS_OFF_SHELF_ERROR.code -> ErrorMessage.Dialog("该商品已下架")
                    else -> ErrorMessage.Toast(e.message)
                }
                _uiState.update { it.copy(isAddingToCart = false, isAddedToCart = false, errorMessage = errorMessage) }
            }

            else -> {
                Log.e(TAG, e.toString(), e)
                _uiState.update { it.copy(isAddingToCart = false, isAddedToCart = false, errorMessage = ErrorMessage.Toast("${e.message}")) }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}