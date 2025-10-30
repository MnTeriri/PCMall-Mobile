package com.example.pcmallcompose.ui.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.activity.MainActivity
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.ui.Screen

@Composable
fun MainActivityPage() {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Index
    ) {
        composable<Screen.Index> {
            IndexPage(navController)
        }
        composable<Screen.Login> {
            LoginPage(
                onBackClick = {
                    (context as MainActivity).getUserData()
                    navController.popBackStack()
                },
                jumpToRegister = {
                    navController.navigate(Screen.Register) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Screen.Register> {
            RegisterPage(onBackClick = {
                navController.popBackStack(Screen.Register, true)
            })
        }

        composable<Screen.GoodsDetail> {backStackEntry->
            val goodsDetail: Screen.GoodsDetail = backStackEntry.toRoute()
            val goods = remember(goodsDetail.goods) {
                JSON.parseObject(goodsDetail.goods, Goods::class.java)
            }
            GoodsDetailPage(goods)
        }
    }
}