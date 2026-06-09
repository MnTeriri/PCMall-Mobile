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
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.paging.GoodsRemoteMediator
import com.example.pcmallcompose.core.data.repository.GoodsRepository
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.dao.GoodsDao
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val adImageList: List<String> = emptyList(),
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val goodsDao: GoodsDao,
    private val goodsRepository: GoodsRepository
) : ViewModel() {
    companion object {
        private const val TAG: String = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 首页商品：固定参数，ViewModel 生命周期内唯一实例
    @OptIn(ExperimentalPagingApi::class)
    val goodsPagingFlow: Flow<PagingData<Goods>> = Pager(
        config = PagingConfig(pageSize = 10, initialLoadSize = 30),
        remoteMediator = GoodsRemoteMediator("goods_home", "", database, goodsRepository)
    ) {
        goodsDao.pagingSource("goods_home", "")
    }.flow.cachedIn(viewModelScope).map { pagingData ->
        pagingData.map { it.goods }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getGoodsPagingData(label: String, searchValue: String): Flow<PagingData<Goods>> {
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 30),
            remoteMediator = GoodsRemoteMediator(label, searchValue, database, goodsRepository)
        ) {
            database.goodsDao().pagingSource(label, searchValue)
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { it.goods }
        }
    }

    fun getADImageList() {
        viewModelScope.launch(Dispatchers.IO) {
            goodsRepository.getADImageList()
                .onSuccess { data ->
                    _uiState.update { it.copy(adImageList = data) }
                }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(e: Throwable) {
        val msg = if (e is ApiException) {
            Log.w(TAG, e.toString(), e)
            e.message
        } else {
            Log.e(TAG, e.toString(), e)
            e.message.orEmpty()
        }
        _uiState.update { it.copy(errorMessage = ErrorMessage.Toast(msg)) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}