package com.example.pcmallcompose.ui

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Category : Screen()

    @Serializable
    data object Cart : Screen()

    @Serializable
    data object Myself : Screen()

    @Serializable
    data object Index : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object AiChat : Screen()

    @Serializable
    data object Address : Screen()

    @Serializable
    data class AddressEdit(val isNewAddress: Boolean, val address: String) : Screen()

    @Serializable
    data class Order(val tab: OrderTab) : Screen() {
        enum class OrderTab(
            val code: Int,
            val label: String
        ) {
            ALL(-1, "全部"),
            PENDING_PAYMENT(0, "待付款"),
            PENDING_SHIPMENT(1, "待发货"),
            PENDING_RECEIPT(2, "待收货"),
            SUCCESS(3, "交易成功"),
            CANCELED(4, "交易取消"),
            RETURNING(5, "退货中"),
            RETURNED(6, "退货成功");
        }
    }

    @Serializable
    data class GoodsDetail(val goods: String) : Screen()
}