package com.example.pcmallcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pcmallcompose.model.Cart
import com.example.pcmallcompose.paging.CartRemoteMediator
import com.example.pcmallcompose.room.PCMallDatabase
import com.example.pcmallcompose.service.CartService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val cartService: CartService
):ViewModel() {
    @OptIn(ExperimentalPagingApi::class)
    fun getCartPagingData(uid: String): Flow<PagingData<Cart>> {
        return Pager(
            config = PagingConfig(pageSize = 30, initialLoadSize = 30),
            remoteMediator = CartRemoteMediator(database,cartService)
        ) {
            database.cartDao().pagingSource()
        }.flow.cachedIn(viewModelScope)
    }
}