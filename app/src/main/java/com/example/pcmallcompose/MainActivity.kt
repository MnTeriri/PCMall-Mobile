package com.example.pcmallcompose

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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pcmallcompose.model.Address
import com.example.pcmallcompose.model.Screen
import com.example.pcmallcompose.ui.page.CategoryPage
import com.example.pcmallcompose.ui.page.HomePage

class MainActivity : ComponentActivity() {
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
    val iconItems = listOf(
        ImageVector.vectorResource(R.drawable.ic_bottom_home),
        ImageVector.vectorResource(R.drawable.ic_bottom_category),
        ImageVector.vectorResource(R.drawable.ic_bottom_cart),
        ImageVector.vectorResource(R.drawable.ic_bottom_myself),
    )
    val items = listOf(Screen.Home, Screen.Category, Screen.Cart, Screen.Myself)
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        iconItems[index],
                        contentDescription = stringResource(item.resourceId)
                    )
                },
                label = { Text(stringResource(item.resourceId)) },
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
        modifier = Modifier.padding(innerPadding),
    ) {
        composable(Screen.Home.route) {
            HomePage()
        }
        composable(Screen.Category.route) {
            CategoryPage()
        }
        composable(Screen.Cart.route) {

        }
        composable(Screen.Myself.route) {

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