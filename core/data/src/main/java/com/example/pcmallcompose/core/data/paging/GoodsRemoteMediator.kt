package com.example.pcmallcompose.core.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.APPEND
import androidx.paging.LoadType.PREPEND
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pcmallcompose.core.data.repository.GoodsRepository
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.entity.GoodsEntity
import com.example.pcmallcompose.core.database.entity.RemoteKey

@OptIn(ExperimentalPagingApi::class)
class GoodsRemoteMediator(
    private val label: String,
    private val searchValue: String,
    private val database: PCMallDatabase,
    private val goodsRepository: GoodsRepository
) : RemoteMediator<Int, GoodsEntity>() {
    companion object {
        private const val TAG = "GoodsRemoteMediator"
        private const val TABLE_NAME = "goods"
    }

    private val goodsDao = database.goodsDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, GoodsEntity>): MediatorResult {
        try {
            val loadKey = when (loadType) {
                REFRESH -> 1
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    val remoteKey = remoteKeyDao.remoteKeyByQuery(TABLE_NAME, "${label}_${searchValue}")
                    remoteKey.currentPage + 1
                }
            }
            val data = goodsRepository.searchGoodsList(searchValue, loadKey, state.config.pageSize).getOrNull()
            Log.d(TAG, "loadType : $loadType, loadPage : $loadKey, data : $data")

            database.withTransaction {
                if (loadType == REFRESH) {
                    remoteKeyDao.deleteByQuery(TABLE_NAME, "${label}_${searchValue}")
                    goodsDao.clearAll(label, searchValue)
                }

                if (data.isNullOrEmpty()) {
                    return@withTransaction
                }

                remoteKeyDao.insertOrReplace(RemoteKey(TABLE_NAME, "${label}_${searchValue}", loadKey))
                goodsDao.insertAll(data.map { goods ->
                    GoodsEntity(label = label, searchValue = searchValue, goods = goods)
                })
            }
            return MediatorResult.Success(endOfPaginationReached = data.isNullOrEmpty())
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
            return MediatorResult.Error(e)
        }
    }
}