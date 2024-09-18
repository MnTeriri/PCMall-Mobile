package com.example.pcmallcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pcmallcompose.model.Screen
import com.example.pcmallcompose.ui.page.LoginPage
import com.example.pcmallcompose.ui.page.RegisterPage
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PCMallComposeTheme {
                LoginRegisterActivityPage(this)
            }
        }
    }
}

@Composable
fun LoginRegisterActivityPage(activity: ComponentActivity) {
    val navHostController = rememberNavController()
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navHostController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginPage(
                onBackClick = { activity.finish() },
                jumpToRegister = {
                    navHostController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterPage(onBackClick = {
                navHostController.popBackStack()
            })
        }
    }
}