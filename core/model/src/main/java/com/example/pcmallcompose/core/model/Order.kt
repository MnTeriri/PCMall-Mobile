package com.example.pcmallcompose.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal
import java.time.LocalDateTime

data class Order(
    val id: Int,
    val oid: String,//订单编号
    val uid: String,//用户编号
    val goodsList: List<Goods>,//订单商品信息
    val address: Address,//地址信息
    val price: BigDecimal,//总金额
    val status: OrderState, //状态（0待付款、1待发货、2待收货、3交易成功、4交易取消、5退货中、6退货成功）
    val createTime: LocalDateTime,//创建时间
    val payTime: LocalDateTime? = null,//付款时间
    val sendTime: LocalDateTime? = null,//发货时间
    val finishTime: LocalDateTime? = null,//完成时间
) {
    enum class OrderState(
        @field:JsonValue
        val code: Int,
        val label: String
    ) {
        PENDING_PAYMENT(0, "待付款"),
        PENDING_SHIPMENT(1, "待发货"),
        PENDING_RECEIPT(2, "待收货"),
        SUCCESS(3, "交易成功"),
        CANCELED(4, "交易取消"),
        RETURNING(5, "退货中"),
        RETURNED(6, "退货成功");

        companion object {
            @JsonCreator
            fun fromCode(code: Int): OrderState? {
                for (state in entries) {
                    if (state.code == code) {
                        return state
                    }
                }
                return null
            }
        }
    }
}