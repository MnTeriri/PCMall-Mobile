package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.paging.GoodsRemoteMediator
import com.example.pcmallcompose.room.PCMallDatabase
import com.example.pcmallcompose.service.GoodsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val goodsService: GoodsService
) : ViewModel() {
    companion object {
        private const val TAG: String = "HomeViewModel"
    }

    private val goodsList: LiveData<Goods> = MutableLiveData()

    @OptIn(ExperimentalPagingApi::class)
    fun getGoodsPagingData(
        label: String = "goods_home",
        searchValue: String = "",
    ): Flow<PagingData<Goods>> {
        Log.d(TAG, searchValue)
        return Pager(
            config = PagingConfig(pageSize = 15, initialLoadSize = 30),
            remoteMediator = GoodsRemoteMediator(
                label = label,
                searchValue = searchValue,
                database = database,
                goodsService = goodsService
            )
        ) {
            database.goodsDao().pagingSource(label, searchValue)
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { it.goods }
        }
    }
}