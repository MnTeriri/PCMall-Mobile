package com.example.pcmallcompose.core.model

data class OrderAddress(
    val province: String,
    val city: String,
    val district: String,
    val addressDetail: String,
    val receiverName: String,
    val phone: String,
)