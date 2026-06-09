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
import com.example.pcmallcompose.core.data.repository.AddressRepository
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.entity.AddressEntity
import com.example.pcmallcompose.core.database.entity.RemoteKey

@OptIn(ExperimentalPagingApi::class)
class AddressRemoteMediator(
    private val uid: String,
    private val database: PCMallDatabase,
    private val addressRepository: AddressRepository
) : RemoteMediator<Int, AddressEntity>() {

    companion object {
        private const val TAG = "AddressRemoteMediator"
        private const val TABLE_NAME = "address"
    }

    private val addressDao = database.addressDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, AddressEntity>): MediatorResult {
        try {
            val loadKey = when (loadType) {
                REFRESH -> 1
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> return MediatorResult.Success(endOfPaginationReached = true) //第一次加载就是所有，没有APPEND
            }
            val data = addressRepository.searchAddressList(uid).getOrNull()
            Log.d(TAG, "loadType : $loadType, loadPage : $loadKey, data : $data")

            database.withTransaction {
                if (loadType == REFRESH) {
                    remoteKeyDao.deleteByQuery(TABLE_NAME, uid)
                    addressDao.clearAll(uid)
                }
                if (data.isNullOrEmpty()) {
                    return@withTransaction
                }
                remoteKeyDao.insertOrReplace(RemoteKey(TABLE_NAME, uid, loadKey))
                addressDao.insertAll(data.map { AddressEntity.fromAddress(it) })
            }
            return MediatorResult.Success(endOfPaginationReached = data.isNullOrEmpty())
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
            return MediatorResult.Error(e)
        }
    }
}