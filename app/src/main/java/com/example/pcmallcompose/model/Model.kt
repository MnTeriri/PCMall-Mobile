package com.example.pcmallcompose.model

import androidx.annotation.StringRes
import com.example.pcmallcompose.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    data object Home : Screen("主页", R.string.title_home)
    data object Category : Screen("分类", R.string.title_category)
    data object Cart : Screen("购物车", R.string.title_cart)
    data object Myself : Screen("我的", R.string.title_myself)
}

