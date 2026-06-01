package com.example.pcmallcompose.core.model.dto

data class AddressDTO(
    val id: Int? = null,
    val uid: String,
    val province: String,
    val city: String,
    val district: String,
    val addressDetail: String,
    val receiverName: String,
    val phone: String,
    val isDefault: Int,
)