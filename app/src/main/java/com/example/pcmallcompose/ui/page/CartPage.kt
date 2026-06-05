package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cn.hutool.core.util.NumberUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.application.LocalUserData
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.model.Goods.GoodsState
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.ErrorMessage
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.ui.theme.PriceColor
import com.example.pcmallcompose.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(
    isIndexPage: Boolean = true,
    onBackClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onDetailClick: (goods: Goods) -> Unit = {},
    onCreateOrderClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val userData = LocalUserData.current
    val viewModel: CartViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lazyPagingItems = viewModel.getCartPagingData().collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val selectedInfo by remember {
        derivedStateOf {
            var count = 0
            var total = BigDecimal.ZERO

            for (i in 0 until lazyPagingItems.itemCount) {
                val cart = lazyPagingItems[i] ?: continue
                if (cart.isSelect == 1) {
                    val price = cart.goods.price
                    total += price.multiply(BigDecimal(cart.count))
                    count++
                }
            }
            Pair(count, total)
        }
    }

    val isAllSelected by remember {
        derivedStateOf {
            var count = 0
            for (i in 0 until lazyPagingItems.itemCount) {
                val cart = lazyPagingItems[i] ?: continue
                if (cart.isSelect == 1) {
                    count++
                }
            }
            count == lazyPagingItems.itemCount
        }
    }

    // 操作成功 → 刷新分页数据
    LaunchedEffect(uiState.shouldRefresh) {
        if (uiState.shouldRefresh) {
            lazyPagingItems.refresh()
            viewModel.refreshConsumed()
        }
    }

    // 操作错误 → Toast / Dialog
    LaunchedEffect(uiState.errorMessage) {
        when (val msg = uiState.errorMessage) {
            is ErrorMessage.Dialog -> {
                MessageDialog(context, SweetAlertDialog.WARNING_TYPE, msg.text).show()
            }

            is ErrorMessage.Toast -> {
                Toast.makeText(context, msg.text, Toast.LENGTH_SHORT).show()
            }

            null -> {}
        }
        viewModel.errorMessageShown()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CartPageTopBar(
                scrollBehavior = scrollBehavior,
                isIndexPage = isIndexPage,
                onBackClick = onBackClick,
                onAiClick = onAiClick
            )
        },
        bottomBar = {
            if (userData != null) {
                CartPageBottomBar(
                    isAllSelected = isAllSelected,
                    selectedCount = selectedInfo.first,
                    totalPrice = selectedInfo.second,
                    onSelectAllClick = {
                        val newState = if (isAllSelected) 0 else 1
                        viewModel.selectAllCart(userData.uid, newState)
                    },
                    onCreateOrderClick = onCreateOrderClick
                )
            }
        }
    ) { innerPadding ->
        if (userData == null) {
            EmptyCartLoginView(
                modifier = Modifier.padding(innerPadding),
                onLoginClick = onLoginClick
            )
        } else {
            CartListView(
                modifier = Modifier.padding(innerPadding),
                lazyPagingItems = lazyPagingItems,
                onClick = { onDetailClick(it.goods) },
                onSelectClick = {
                    val newState = if (it.isSelect == 1) 0 else 1
                    viewModel.selectCart(it.id, newState)
                },
                onAddClick = { viewModel.addCartCount(it.id) },
                onSubClick = { viewModel.subCartCount(it.id) },
                onDeleteClick = { viewModel.deleteCart(it.id) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPageTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    isIndexPage: Boolean,
    onBackClick: () -> Unit,
    onAiClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(text = "购物车") },
        navigationIcon = {
            if (!isIndexPage) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onAiClick) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun CartPageBottomBar(
    isAllSelected: Boolean,
    selectedCount: Int,
    totalPrice: BigDecimal,
    onSelectAllClick: () -> Unit,
    onCreateOrderClick: () -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：全选
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isAllSelected,
                    onClick = onSelectAllClick
                )
                Text(text = "全选", fontSize = 14.sp)
            }

            // 右侧：合计价格 + 结算按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column {
                    Row {
                        Text(
                            text = "合计: ",
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "¥${totalPrice.setScale(2)}",
                            fontSize = 15.sp,
                            color = PriceColor
                        )
                    }
                    Text(
                        text = "已选 $selectedCount 件",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.width(10.dp))
                Button(
                    modifier = Modifier.size(height = 45.dp, width = 110.dp),
                    onClick = onCreateOrderClick,
                    enabled = selectedCount > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = PriceColor)
                ) {
                    Text(if (selectedCount > 0) "结算($selectedCount)" else "结算")
                }
            }
        }
    }
}

@Composable
private fun EmptyCartLoginView(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PersonOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "登录后查看购物车",
                fontSize = 15.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = onLoginClick) {
                Text("去登录")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CartListView(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<Cart>,
    onClick: (cart: Cart) -> Unit,
    onSelectClick: (cart: Cart) -> Unit,
    onAddClick: (cart: Cart) -> Unit,
    onSubClick: (cart: Cart) -> Unit,
    onDeleteClick: (cart: Cart) -> Unit
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
                val cart = lazyPagingItems[it]!!

                CartItemView(
                    cart = cart,
                    onClick = { onClick(cart) },
                    onSelectClick = { onSelectClick(cart) },
                    onAddClick = { onAddClick(cart) },
                    onSubClick = { onSubClick(cart) },
                    onDeleteClick = { onDeleteClick(cart) }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CartItemView(
    cart: Cart,
    onClick: () -> Unit,
    onSelectClick: () -> Unit,
    onAddClick: () -> Unit,
    onSubClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val enabled = (cart.goods.status == GoodsState.NORMAL && cart.goods.isDelete == 0)
    var message = cart.goods.description

    if (cart.goods.status == GoodsState.OUT_OF_STOCK) {
        message = "该商品缺货！"
    } else if (cart.goods.status == GoodsState.OFF_SHELF) {
        message = "该商品已下架！"
    } else if (cart.goods.isDelete == 1) {
        message = "该商品已删除！"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(top = 8.dp, start = 10.dp, end = 10.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onDeleteClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                enabled = enabled,
                selected = cart.isSelect == 1,
                onClick = onSelectClick
            )

            GlideImage(
                model = NetworkModule.IMAGE_URL + cart.goods.image,
                modifier = Modifier.size(100.dp),
                contentDescription = null,
                failure = placeholder(R.drawable.test_image)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = "${cart.goods.brand.bname}${cart.goods.gname}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                if (!enabled) {
                    return@Column
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "¥${NumberUtil.mul(cart.goods.price, cart.count).setScale(2)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PriceColor
                    )
                    CartCountButton(
                        count = cart.count,
                        onAddClick = onAddClick,
                        onSubClick = onSubClick
                    )
                }
            }

        }

    }
}

@Composable
fun CartCountButton(
    modifier: Modifier = Modifier,
    count: Int,
    onAddClick: () -> Unit,
    onSubClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF5F5F5),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 减号
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = onSubClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    contentDescription = "减少",
                    modifier = Modifier.size(18.dp)
                )
            }

            // 数量
            Text(
                text = "$count",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(32.dp)
            )

            // 加号
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = onAddClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "增加",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingCartPagePreview() {
    PCMallComposeTheme {
        CartPage()
    }
}