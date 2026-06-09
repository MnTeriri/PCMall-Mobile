package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.database.dao.CartDao
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.network.service.CartService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartService: CartService,
    private val cartDao: CartDao
) {
    suspend fun searchAllCart(
        uid: String,
        currentPage: Int,
        pageSize: Int
    ): Result<List<Cart>> = apiCall {
        cartService.searchAllCart(uid, currentPage, pageSize).data ?: emptyList()
    }

    suspend fun searchSelectCart(uid: String): Result<List<Cart>> = apiCall {
        cartService.searchSelectCart(uid).data ?: emptyList()
    }

    suspend fun addCartCount(cartId: Int): Result<Unit> = apiCall {
        cartService.addCartCount(cartId)
        Unit
    }.onSuccess { cartDao.addCartCount(cartId) }

    suspend fun subCartCount(cartId: Int): Result<Unit> = apiCall {
        cartService.subCartCount(cartId)
        Unit
    }.onSuccess { cartDao.subCartCount(cartId) }

    suspend fun selectCart(cartId: Int, isSelect: Int): Result<Unit> = apiCall {
        cartService.selectCart(cartId, isSelect)
        Unit
    }.onSuccess { cartDao.updateSelectCart(cartId, isSelect) }

    suspend fun selectAllCart(uid: String, isSelect: Int): Result<Unit> = apiCall {
        cartService.selectAllCart(uid, isSelect)
    }

    suspend fun deleteCart(cartId: Int): Result<Unit> = apiCall {
        cartService.deleteCart(cartId)
        Unit
    }.onSuccess { cartDao.deleteById(cartId) }

    suspend fun addCart(goodsId: Int, uid: String): Result<Unit> = apiCall {
        cartService.addCart(goodsId, uid)
    }
}