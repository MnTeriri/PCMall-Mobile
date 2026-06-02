package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.core.model.Order.OrderState
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(tableName = "order")
data class OrderEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "oid") val oid: String,
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "goods_list") val goodsList: List<Goods>,
    @ColumnInfo(name = "address") val address: Address,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "status") val status: OrderState,
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime,
    @ColumnInfo(name = "pay_time") val payTime: LocalDateTime? = null,
    @ColumnInfo(name = "send_time") val sendTime: LocalDateTime? = null,
    @ColumnInfo(name = "finish_time") val finishTime: LocalDateTime? = null,
) {
    fun toOrder(): Order = Order(
        id = id,
        oid = oid,
        uid = uid,
        goodsList = goodsList,
        address = address,
        price = price,
        status = status,
        createTime = createTime,
        payTime = payTime,
        sendTime = sendTime,
        finishTime = finishTime
    )

    companion object {
        @JvmStatic
        fun fromOrder(order: Order): OrderEntity = OrderEntity(
            id = order.id,
            oid = order.oid,
            uid = order.uid,
            goodsList = order.goodsList,
            address = order.address,
            price = order.price,
            status = order.status,
            createTime = order.createTime,
            payTime = order.payTime,
            sendTime = order.sendTime,
            finishTime = order.finishTime
        )
    }
}