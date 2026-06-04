package com.example.pcmallcompose.core.model

import java.math.BigDecimal

data class OrderGoods(
    val id: Int,
    val cid: Int,
    val category: Category,
    val bid: Int,
    val brand: Brand,
    val gname: String,
    val image: String,
    val price: BigDecimal,
    val discount: BigDecimal,
    val count: Int,
    val description: String
)