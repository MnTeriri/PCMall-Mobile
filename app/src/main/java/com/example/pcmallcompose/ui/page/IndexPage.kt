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
import com.example.pcmallcompose.model.Screen

@Composable
fun IndexPage(mainNavController: NavHostController) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            IndexPageBottomBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Home.route) {
                HomePage()
            }
            composable(Screen.Category.route) {
                CategoryPage()
            }
            composable(Screen.Cart.route) {
                CartPage()
            }
            composable(Screen.Myself.route) {
                MySelfPage {
                    mainNavController.navigate("login") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

@Composable
fun IndexPageBottomBar(navController: NavHostController) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

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
                        imageVector = ImageVector.vectorResource(item.drawableResId),
                        contentDescription = stringResource(item.stringResId)
                    )
                },
                label = { Text(stringResource(item.stringResId)) },
                selected = selectedItem == index,
                onClick = {
                    navController.popBackStack()
                    navController.navigate(item.route) {
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