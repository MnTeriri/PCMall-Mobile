package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "HomePage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: HomeViewModel = hiltViewModel()) {
    var presses by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("HomePage")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            GoodsListView(viewModel.getGoodsPagingData().collectAsLazyPagingItems())
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GoodsListView(lazyPagingItems: LazyPagingItems<Goods>) {
    val coroutineScope = rememberCoroutineScope()
    //是否在刷新
    var isRefreshing by remember { mutableStateOf(false) }
    //下拉刷新的状态管理
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {//刷新时所做动作
            coroutineScope.launch {
                isRefreshing = true//设置正在刷新
                delay(1000)
                lazyPagingItems.refresh()
                isRefreshing = false //刷新业务执行完毕后修改状态
            }
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ) {
        LazyColumn {
            items(
                count = lazyPagingItems.itemCount,
                key = { index ->
                    lazyPagingItems[index]?.id!!
                }
            ) { index ->
                val goods = lazyPagingItems[index]
                if (goods != null) {
                    GoodsItemView(goods)
                } else {
                    Text(text = "")
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun GoodsItemView(goods: Goods) {
    Text(
        modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 20.dp),
        text = "${goods.id},${goods.gname}"
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingHomePagePagePreview() {
    PCMallComposeTheme {
        HomePage()
    }
}