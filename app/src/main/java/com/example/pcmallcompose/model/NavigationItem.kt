package com.example.pcmallcompose.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.pcmallcompose.R

sealed class NavigationItem(
    val route: String,
    @StringRes val stringResId: Int,
    @DrawableRes val drawableResId: Int
) {
    data object Home : NavigationItem("主页", R.string.title_home, R.drawable.ic_bottom_home)
    data object Category : NavigationItem("分类", R.string.title_category, R.drawable.ic_bottom_category)
    data object Cart : NavigationItem("购物车", R.string.title_cart, R.drawable.ic_bottom_cart)
    data object Myself : NavigationItem("我的", R.string.title_myself, R.drawable.ic_bottom_myself)
}