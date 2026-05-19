package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
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

    var uiState by mutableStateOf(HomeUiState())
        private set

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
                if (data != null) {
                    uiState = uiState.copy(adImageList = data)
                } else {
                    uiState = uiState.copy(adImageList = emptyList())
                }
            } catch (e: Exception) {

            }
        }
    }
}