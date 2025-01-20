package com.example.pcmallcompose.ui.page

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pcmallcompose.model.Screen

@Composable
fun MainActivityPage() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        "index"
    ) {
        composable("index") {
            IndexPage(navController)
        }
        composable(Screen.Login.route) {
            LoginPage(
                onBackClick = { navController.popBackStack() },
                jumpToRegister = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterPage(onBackClick = {
                navController.popBackStack(Screen.Register.route,true)
            })
        }
    }
}