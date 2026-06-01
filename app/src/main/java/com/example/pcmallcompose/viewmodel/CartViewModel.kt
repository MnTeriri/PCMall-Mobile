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
import com.example.pcmallcompose.core.data.paging.CartRemoteMediator
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.dao.CartDao
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.model.response.ResponseCode
import com.example.pcmallcompose.core.network.service.CartService
import com.example.pcmallcompose.core.network.utils.RetrofitUtils
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
import retrofit2.HttpException

data class CartUiState(
    val errorMessage: ErrorMessage? = null,      // 加减选操作的瞬态反馈
    val shouldRefresh: Boolean = false,          // 操作成功后触发 Paging 刷新
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val cartService: CartService,
    private val cartDao: CartDao,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        const val TAG = "CartViewModel"
    }

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private var cachedKey: String = ""
    private var cachedFlow: Flow<PagingData<Cart>> = flowOf(PagingData.empty())

    @OptIn(ExperimentalPagingApi::class)
    fun getCartPagingData(): Flow<PagingData<Cart>> {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            return flowOf(PagingData.empty())
        }

        val version = userSession.cartVersion()
        val key = "$uid--$version"
        if (key == cachedKey) {
            return cachedFlow
        }

        cachedKey = key
        cachedFlow = Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 30),
            remoteMediator = CartRemoteMediator(uid, database, cartService)
        ) {
            database.cartDao().pagingSource(uid)
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { it.toCart() }
        }
        return cachedFlow
    }

    fun addCartCount(cartId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartService.addCartCount(cartId)
                cartDao.addCartCount(cartId)
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun subCartCount(cartId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartService.subCartCount(cartId)
                cartDao.subCartCount(cartId)
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun selectCart(cartId: Int, isSelect: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartService.selectCart(cartId, isSelect)
                cartDao.updateSelectCart(cartId, isSelect)
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun selectAllCart(uid: String, isSelect: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartService.selectAllCart(uid, isSelect)
                _uiState.update { it.copy(shouldRefresh = true) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun deleteCart(cartId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cartService.deleteCart(cartId)
                cartDao.deleteById(cartId)
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun refreshConsumed() {
        _uiState.update { it.copy(shouldRefresh = false) }
    }

    private fun catchHttpException(e: HttpException) {
        val response = RetrofitUtils.getErrorMessage(e)
        if (response == null) {
            catchException(e)
            return
        }

        Log.w(TAG, "$e: $response", e)

        val errorMessage = when (response.code) {
            ResponseCode.ENTITY_NOT_FOUND.code -> {
                _uiState.update { it.copy(shouldRefresh = true) }
                ErrorMessage.Dialog("查询信息失败")
            }
            ResponseCode.CART_MIN_COUNT_ERROR.code -> ErrorMessage.Dialog("已达最小数量")
            ResponseCode.GOODS_NOT_ENOUGH_ERROR.code -> ErrorMessage.Dialog("商品库存不足")
            ResponseCode.GOODS_OFF_SHELF_ERROR.code -> ErrorMessage.Dialog("该商品已下架")
            ResponseCode.CART_GOODS_ERROR.code -> ErrorMessage.Dialog("购物车状态异常")
            else -> ErrorMessage.Toast(response.message)
        }

        _uiState.update { it.copy(errorMessage = errorMessage) }
    }

    private fun catchException(e: Exception) {
        Log.e(TAG, e.toString(), e)
        _uiState.update {
            it.copy(errorMessage = ErrorMessage.Toast("${e.message}"))
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}