package com.example.pcmallcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pcmallcompose.core.data.paging.CartRemoteMediator
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.network.service.CartService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@HiltViewModel
class CartViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val cartService: CartService
) : ViewModel() {
    @OptIn(ExperimentalPagingApi::class)
    fun getCartPagingData(uid: String): Flow<PagingData<Cart>> {
        return Pager(
            config = PagingConfig(pageSize = 30, initialLoadSize = 30),
            remoteMediator = CartRemoteMediator("000000000", database, cartService)
        ) {
            database.cartDao().pagingSource()
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { it.toCart() }
        }
    }
}