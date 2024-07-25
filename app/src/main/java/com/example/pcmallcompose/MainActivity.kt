package com.example.pcmallcompose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pcmallcompose.model.NavigationItem
import com.example.pcmallcompose.service.GoodsService
import com.example.pcmallcompose.ui.page.CategoryPage
import com.example.pcmallcompose.ui.page.HomePage
import com.example.pcmallcompose.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val TAG: String = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var goodsService: GoodsService

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PCMallComposeTheme {
                MainActivityPage()
            }
        }
    }
}

@Composable
fun MainActivityPage() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            MainActivityBottomBar(navController)
        }
    ) { innerPadding ->
        MainActivityNavHost(navController, innerPadding)
    }
}

@Composable
fun MainActivityBottomBar(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val navigationItems = listOf(
        NavigationItem.Home,
        NavigationItem.Category,
        NavigationItem.Cart,
        NavigationItem.Myself
    )
    NavigationBar {
        navigationItems.forEachIndexed { index, item ->
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
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                    selectedItem = index
                }
            )
        }
    }
}

@Composable
fun MainActivityNavHost(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "主页",
        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
    ) {
        composable(NavigationItem.Home.route) {
            HomePage()
        }
        composable(NavigationItem.Category.route) {
            CategoryPage()
        }
        composable(NavigationItem.Cart.route) {

        }
        composable(NavigationItem.Myself.route) {

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PCMallComposeTheme {
        MainActivityPage()
    }
}