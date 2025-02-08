package com.example.pcmallcompose.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.pcmallcompose.R

sealed class Screen(
    val route: String,
    @StringRes val stringResId: Int,
    @DrawableRes val drawableResId: Int
) {
    data object Home : Screen("home", R.string.title_home, R.drawable.ic_bottom_home)
    data object Category : Screen("category", R.string.title_category, R.drawable.ic_bottom_category)
    data object Cart : Screen("cart", R.string.title_cart, R.drawable.ic_bottom_cart)
    data object Myself : Screen("myself", R.string.title_myself, R.drawable.ic_bottom_myself)
    data object Login : Screen("login",  R.string.title_home, R.drawable.ic_bottom_home)
    data object Register : Screen("register",  R.string.title_home, R.drawable.ic_bottom_home)
}