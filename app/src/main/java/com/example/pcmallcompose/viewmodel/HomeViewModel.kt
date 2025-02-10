package com.example.pcmallcompose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.paging.GoodsPagingSource
import com.example.pcmallcompose.service.GoodsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val goodsService: GoodsService
) : ViewModel() {
    private val goodsList: LiveData<Goods> = MutableLiveData()
    fun getGoodsPagingData(
        searchValue: String = "",
        pageSize: Int = 20,
        initialLoadSize: Int = pageSize
    ): Flow<PagingData<Goods>> {
        return Pager(
            PagingConfig(pageSize = pageSize, initialLoadSize = initialLoadSize)
        ) {
            GoodsPagingSource(goodsService, searchValue)
        }.flow.cachedIn(viewModelScope)
    }
}