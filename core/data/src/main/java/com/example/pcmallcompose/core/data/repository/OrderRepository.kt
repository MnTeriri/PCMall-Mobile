package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.database.dao.OrderDao
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.core.network.service.OrderService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderService: OrderService,
    private val orderDao: OrderDao
) {
    suspend fun searchOrderList(
        searchValue: String,
        uid: String,
        type: Int,
        currentPage: Int,
        pageSize: Int
    ): Result<List<Order>> = apiCall {
        orderService.searchOrderList(searchValue, uid, type, currentPage, pageSize).data ?: emptyList()
    }

    suspend fun getRecordsFiltered(
        searchValue: String,
        uid: String,
        type: Int
    ): Result<Long> = apiCall {
        orderService.getRecordsFiltered(searchValue, uid, type).data ?: 0L
    }

    suspend fun createOrder(uid: String, aid: Int): Result<Unit> = apiCall {
        orderService.createOrder(uid, aid)
    }

    suspend fun payOrder(oid: String): Result<Unit> = apiCall {
        orderService.payOrder(oid)
        Unit
    }.onSuccess { orderDao.updateStatusByOid(oid, Order.OrderState.PENDING_SHIPMENT) }

    suspend fun finishOrder(oid: String): Result<Unit> = apiCall {
        orderService.finishOrder(oid)
        Unit
    }.onSuccess { orderDao.updateStatusByOid(oid, Order.OrderState.SUCCESS) }

    suspend fun cancelOrder(oid: String): Result<Unit> = apiCall {
        orderService.cancelOrder(oid)
        Unit
    }.onSuccess { orderDao.updateStatusByOid(oid, Order.OrderState.CANCELED) }

    suspend fun refundOrder(oid: String): Result<Unit> = apiCall {
        orderService.refundOrder(oid)
        Unit
    }.onSuccess { orderDao.updateStatusByOid(oid, Order.OrderState.RETURNING) }
}