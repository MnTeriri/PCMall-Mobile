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
import com.example.pcmallcompose.core.data.paging.GoodsRemoteMediator
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.network.service.GoodsService
import com.example.pcmallcompose.core.network.service.ImageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val adImageList: List<String> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val goodsService: GoodsService,
    private val imageService: ImageService
) : ViewModel() {
    companion object {
        private const val TAG: String = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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

    fun getADImageList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = imageService.getADImageList().data
                _uiState.update { it.copy(adImageList = data ?: emptyList()) }
            } catch (e: Exception) {

            }
        }
    }
}