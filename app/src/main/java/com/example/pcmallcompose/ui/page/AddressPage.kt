package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.AddressViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddressPage(
    isEmbedded: Boolean = false,
    onBackClick: () -> Unit = {},
    onAddAddressClick: () -> Unit = {},
    onUpdateAddressClick: (Address) -> Unit = {},
) {
    val viewModel: AddressViewModel = hiltViewModel()
    val lazyPagingItems = viewModel.getAddressPagingData().collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        lazyPagingItems.refresh()//每次都刷新
    }

    if (isEmbedded) {
        // ModalBottomSheet 模式 — 无 TopAppBar，底部栏用 weight 推到底
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            AddressListView(
                modifier = Modifier.weight(1f),
                lazyPagingItems = lazyPagingItems,
                onEditClick = onUpdateAddressClick,
            )
            AddressPageBottomBar(onAddAddressClick)
        }
    } else {
        Scaffold(
            topBar = { AddressPageTopBar(onBackClick) },
            bottomBar = { AddressPageBottomBar(onAddAddressClick) }
        ) { innerPadding ->
            AddressListView(
                modifier = Modifier.padding(innerPadding),
                lazyPagingItems = lazyPagingItems,
                onEditClick = onUpdateAddressClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressPageTopBar(
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text("地址管理") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun AddressPageBottomBar(
    onAddAddressClick: () -> Unit,
){
    BottomAppBar(
        containerColor = Color.White
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = onAddAddressClick
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("添加新地址")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddressListView(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<Address>,
    onEditClick: (Address) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }//是否在刷新
    val state = rememberPullToRefreshState()//下拉刷新的状态管理

    val onRefresh: () -> Unit = {
        coroutineScope.launch {
            isRefreshing = true//设置正在刷新
            delay(1000)
            lazyPagingItems.refresh()
            isRefreshing = false //刷新业务执行完毕后修改状态
        }
    }

    PullToRefreshBox(
        modifier = modifier,
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = state,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) {
                val address = lazyPagingItems[it]!!
                AddressItemView(
                    address = address,
                    onEditClick = { onEditClick(address) }
                )
            }
        }
    }

}

@Composable
fun AddressItemView(
    address: Address,
    onEditClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 地区 + 默认标签
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${address.province}${address.city}${address.district}",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (address.isDefault == 1) {
                        Spacer(Modifier.width(10.dp))
                        SuggestionChip(
                            modifier = Modifier.height(25.dp),
                            onClick = {},
                            label = {
                                Text(text = "默认", fontSize = 12.sp, color = Color.White)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            border = null,
                            shape = RoundedCornerShape(5.dp)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // 详细地址
                Text(
                    text = address.addressDetail,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(10.dp))

                // 收件人 + 电话
                Text(
                    text = "${address.receiverName} ${address.phone}",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 编辑按钮
            TextButton(
                onClick = onEditClick,
                modifier = Modifier
                    .width(60.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = "编辑", fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddressPagePreview() {
    PCMallComposeTheme {
        AddressPage()
    }
}