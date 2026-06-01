package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString
import com.example.pcmallcompose.R
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.ui.Screen

@Composable
fun MainActivityPage() {
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
                onBackClick = { navController.popBackStack() },
                onRegisterClick = {
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

        composable<Screen.GoodsDetail> { backStackEntry ->
            val goodsDetail: Screen.GoodsDetail = backStackEntry.toRoute()
            val goods = remember(goodsDetail.goods) {
                goodsDetail.goods.parseObject<Goods>()
            }
            GoodsDetailPage(
                goods = goods,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.AiChat> {
            AiChatPage(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() }
            )
        }

        composable<Screen.Address> {
            AddressPage(
                onBackClick = { navController.popBackStack() },
                onAddAddressClick = { navController.navigate(Screen.AddressEdit) }
            )
        }

        composable<Screen.AddressEdit> {
            AddressEditPage(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun IndexPage(
    mainNavController: NavHostController
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            IndexPageBottomBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable<Screen.Home> {
                HomePage(
                    onDetailClick = { mainNavController.navigate(Screen.GoodsDetail(it.toJSONString())) },
                    onAiClick = { mainNavController.navigate(Screen.AiChat) }
                )
            }
            composable<Screen.Category> {
                CategoryPage()
            }
            composable<Screen.Cart> {
                CartPage(
                    onBackClick = { mainNavController.popBackStack() },
                    onAiClick = { mainNavController.navigate(Screen.AiChat) },
                    onLoginClick = {
                        mainNavController.navigate(Screen.Login) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onDetailClick = { mainNavController.navigate(Screen.GoodsDetail(it.toJSONString())) }
                )
            }
            composable<Screen.Myself> {
                MySelfPage(
                    onLoginClick = {
                        mainNavController.navigate(Screen.Login) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAddressClick = {
                        mainNavController.navigate(Screen.Address)
                    }
                )
            }
        }
    }
}

@Composable
fun IndexPageBottomBar(
    navController: NavHostController
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    val labels = listOf(
        R.string.title_home,
        R.string.title_category,
        R.string.title_cart,
        R.string.title_myself,
    )

    val images = listOf(
        R.drawable.ic_bottom_home,
        R.drawable.ic_bottom_category,
        R.drawable.ic_bottom_cart,
        R.drawable.ic_bottom_myself,
    )

    val items = listOf(
        Screen.Home,
        Screen.Category,
        Screen.Cart,
        Screen.Myself
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(images[index]),
                        contentDescription = stringResource(labels[index])
                    )
                },
                label = { Text(stringResource(labels[index])) },
                selected = selectedItem == index,
                onClick = {
//                    navController.popBackStack()
                    navController.navigate(item) {
                        //进入界面时，清空栈内popUpTo ID到栈顶之间的所有节点，避免节点持续增加
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true//用于页面状态的恢复
                        }
                        launchSingleTop = true//避免多次重复点击产生多个实例
                        restoreState = true//再次点击之前的Item时，恢复之前的状态
                    }
                    selectedItem = index
                }
            )
        }
    }
}