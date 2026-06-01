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
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.entity.CartEntity
import com.example.pcmallcompose.core.database.entity.RemoteKey
import com.example.pcmallcompose.core.network.service.CartService

@OptIn(ExperimentalPagingApi::class)
class CartRemoteMediator(
    private val uid: String,
    private val database: PCMallDatabase,
    private val cartService: CartService
) : RemoteMediator<Int, CartEntity>() {
    companion object {
        private const val TAG = "CartRemoteMediator"
        private const val TABLE_NAME = "cart"
    }

    private val cartDao = database.cartDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, CartEntity>): MediatorResult {
        try {
            val loadKey = when (loadType) {
                //刷新直接从第一页开始
                REFRESH -> 1
                // 前置加载（在列表顶部加载更多）
                // 在此示例中无需前置加载，因为 REFRESH 总是加载第一页。
                // 因此直接返回，表示没有更多数据需要加载。
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    // 后置加载（在列表底部加载更多）
                    val remoteKey = remoteKeyDao.remoteKeyByQuery(TABLE_NAME, uid)
                    remoteKey.currentPage + 1
                }
            }
            val data = cartService.searchAllCart(uid, loadKey, state.config.pageSize).data
            Log.d(TAG, "loadType : $loadType, loadPage : $loadKey, data : $data")

            // 在事务中存储加载的数据和下一个 key，确保它们始终保持一致。
            database.withTransaction {
                if (loadType == REFRESH) {
                    remoteKeyDao.deleteByQuery(TABLE_NAME, uid)
                    cartDao.clearAll(uid)
                }

                // 当列表为空时，代表没有新数据了
                if (data.isNullOrEmpty()) {
                    return@withTransaction
                }

                // 更新该查询的 RemoteKey。
                remoteKeyDao.insertOrReplace(RemoteKey(TABLE_NAME, uid, loadKey))
                // 将新数据插入到数据库中，这会使当前的 PagingData 无效，
                // 让 Paging 可以呈现数据库中的更新。
                cartDao.insertAll(data.map { CartEntity.fromCart(it) })
            }
            return MediatorResult.Success(endOfPaginationReached = data.isNullOrEmpty())
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
            return MediatorResult.Error(e)
        }
    }
}