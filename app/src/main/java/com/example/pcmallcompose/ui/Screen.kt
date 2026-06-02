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
    data object Order : Screen()

    @Serializable
    data class GoodsDetail(val goods: String) : Screen()
}