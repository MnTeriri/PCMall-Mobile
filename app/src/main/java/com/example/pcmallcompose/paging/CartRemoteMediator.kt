package com.example.pcmallcompose.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pcmallcompose.model.Cart
import com.example.pcmallcompose.model.RemoteKey
import com.example.pcmallcompose.room.PCMallDatabase
import com.example.pcmallcompose.service.CartService

@OptIn(ExperimentalPagingApi::class)
class CartRemoteMediator(
    private val uid: String = "000000000",
    private val database: PCMallDatabase,
    private val cartService: CartService
) : RemoteMediator<Int, Cart>() {
    companion object {
        private const val TAG = "BaseRemoteMediator"
        private const val TABLE_NAME = "cart"
    }

    private val cartDao = database.cartDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Cart>): MediatorResult {
        try {
            val loadKey = when (loadType) {
                REFRESH -> 1 //刷新直接从第一页开始
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) //REFRESH将始终加载列表的第一页。立即返回，报告分页结束。
                APPEND -> {
                    val remoteKey = database.withTransaction {
                        remoteKeyDao.remoteKeyByQuery(TABLE_NAME, uid)
                    }
                    if (remoteKey.currentPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.currentPage + 1
                }
            }
            val response = cartService.searchAllCart(uid, loadKey, state.config.pageSize)

            Log.d(TAG, "loadType:$loadType,loadPage:$loadKey,response:$response")

            if (response.data.isNullOrEmpty()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            // 在事务中存储加载的数据和下一个 key，确保它们始终保持一致。
            database.withTransaction {
                if (loadType == REFRESH) {
                    remoteKeyDao.deleteByQuery(TABLE_NAME, uid)
                    cartDao.clearAll()
                }
                // 更新该查询的 RemoteKey。
                remoteKeyDao.insertOrReplace(RemoteKey(TABLE_NAME, uid, loadKey))
                // 将新用户插入到数据库中，这会使当前的 PagingData 无效，
                // 让 Paging 可以呈现数据库中的更新。
                cartDao.insertAll(response.data!!)
            }
            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }
}