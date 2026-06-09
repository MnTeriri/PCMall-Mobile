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
import com.example.pcmallcompose.core.data.repository.OrderRepository
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.entity.OrderEntity
import com.example.pcmallcompose.core.database.entity.RemoteKey

@OptIn(ExperimentalPagingApi::class)
class OrderRemoteMediator(
    private val searchValue: String,
    private val uid: String,
    private val type: Int,
    private val database: PCMallDatabase,
    private val orderRepository: OrderRepository
) : RemoteMediator<Int, OrderEntity>() {
    companion object {
        const val TAG = "OrderRemoteMediator"
        private const val TABLE_NAME = "order"
    }

    private val remoteKeyDao = database.remoteKeyDao()
    private val orderDao = database.orderDao()
    private val label = "${uid}_${type}_${searchValue}"

    override suspend fun load(loadType: LoadType, state: PagingState<Int, OrderEntity>): MediatorResult {
        try {
            val loadKey = when (loadType) {
                REFRESH -> 1
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    val remoteKey = remoteKeyDao.remoteKeyByQuery(TABLE_NAME, label)
                    remoteKey.currentPage + 1
                }
            }
            val data = orderRepository.searchOrderList(searchValue, uid, type, loadKey, state.config.pageSize).getOrNull()
            Log.d(TAG, "loadType : $loadType, loadPage : $loadKey, data : $data")

            // 在事务中存储加载的数据和下一个 key，确保它们始终保持一致。
            database.withTransaction {
                if (loadType == REFRESH) {
                    remoteKeyDao.deleteByQuery(TABLE_NAME, label)
                }

                // 当列表为空时，代表没有新数据了
                if (data.isNullOrEmpty()) {
                    return@withTransaction
                }

                remoteKeyDao.insertOrReplace(RemoteKey(TABLE_NAME, label, loadKey))
                orderDao.insertAll(data.map { OrderEntity.fromOrder(it) })
            }
            return MediatorResult.Success(endOfPaginationReached = data.isNullOrEmpty())
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
            return MediatorResult.Error(e)
        }
    }

}