package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.core.network.service.OrderService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val service: OrderService
) {
    suspend fun searchOrderList(
        searchValue: String,
        uid: String,
        type: Int,
        currentPage: Int,
        pageSize: Int
    ): Result<List<Order>> = apiCall {
        service.searchOrderList(searchValue, uid, type, currentPage, pageSize).data ?: emptyList()
    }

    suspend fun getRecordsFiltered(
        searchValue: String,
        uid: String,
        type: Int
    ): Result<Long> = apiCall {
        service.getRecordsFiltered(searchValue, uid, type).data ?: 0L
    }

    suspend fun createOrder(uid: String, aid: Int): Result<Unit> = apiCall {
        service.createOrder(uid, aid)
    }

    suspend fun payOrder(oid: String): Result<Unit> = apiCall {
        service.payOrder(oid)
    }

    suspend fun finishOrder(oid: String): Result<Unit> = apiCall {
        service.finishOrder(oid)
    }

    suspend fun cancelOrder(oid: String): Result<Unit> = apiCall {
        service.cancelOrder(oid)
    }

    suspend fun refundOrder(oid: String): Result<Unit> = apiCall {
        service.refundOrder(oid)
    }
}