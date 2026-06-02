package com.example.pcmallcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.data.paging.AddressRemoteMediator
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.dao.AddressDao
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.network.service.AddressService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val addressService: AddressService,
    private val addressDao: AddressDao,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        const val TAG = "AddressViewModel"
    }

    private var cachedKey: String = ""
    private var cachedFlow: Flow<PagingData<Address>> = flowOf(PagingData.empty())

    @OptIn(ExperimentalPagingApi::class)
    fun getAddressPagingData(): Flow<PagingData<Address>> {
        val uid = userSession.user.value?.uid
        if (uid.isNullOrEmpty()) {
            return flowOf(PagingData.empty())
        }
        if (uid == cachedKey) {
            return cachedFlow
        }
        cachedKey = uid

        cachedFlow = Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 30),
            remoteMediator = AddressRemoteMediator(uid, database, addressService)
        ) {
            addressDao.pagingSource(uid)
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { it.toAddress() }
        }
        return cachedFlow
    }
}