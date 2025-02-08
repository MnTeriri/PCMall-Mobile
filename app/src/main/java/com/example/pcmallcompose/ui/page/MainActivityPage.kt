package com.example.pcmallcompose.ui.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pcmallcompose.activity.MainActivity
import com.example.pcmallcompose.model.User
import com.example.pcmallcompose.ui.Screen

@Composable
fun MainActivityPage() {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "index"
    ) {
        composable("index") {
            IndexPage(navController)
        }
        composable(Screen.Login.route) {
            LoginPage(
                onBackClick = {
                    (context as MainActivity).getUserData()
                    navController.popBackStack()
                },
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