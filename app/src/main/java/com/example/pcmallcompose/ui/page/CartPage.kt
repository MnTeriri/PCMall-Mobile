package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cn.hutool.core.util.NumberUtil
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.model.Cart
import com.example.pcmallcompose.module.NetworkModule
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.ui.theme.PriceColor
import com.example.pcmallcompose.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val cartViewModel: CartViewModel = hiltViewModel()

    val lazyPagingItems = cartViewModel.getCartPagingData("").collectAsLazyPagingItems()
    lazyPagingItems.itemKey{item->
        println(item)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { CartPageTopBar(scrollBehavior) },
        bottomBar = { CartPageBottomBar() }
    ) { innerPadding ->
        println(innerPadding)
//        CartListView(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPageTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = "购物车") },
        navigationIcon = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun CartPageBottomBar() {
    BottomAppBar(
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(Icons.Filled.Check, contentDescription = "Localized description")
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Localized description",
                )
            }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartListView(
    innerPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<Cart>,
    onClick: (cart: Cart) -> Unit = {},
    onSelectClick: (cart: Cart) -> Unit = {},
    onAddClick: (cart: Cart) -> Unit = {},
    onSubClick: (cart: Cart) -> Unit = {},
    onDeleteClick: (cart: Cart) -> Unit = {}
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
        modifier = Modifier.padding(innerPadding),
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id!! }
            ) { index ->
                val cart = lazyPagingItems[index]!!

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

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun CartItemView(
    cart: Cart,
    onClick: () -> Unit,
    onSelectClick: () -> Unit,
    onAddClick: () -> Unit,
    onSubClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val enabled = (cart.goods?.status == 0 && cart.goods?.isDelete == 0)
    var message = cart.goods?.description

    if (cart.goods?.status == 1) {
        message = "该商品缺货！"
    } else if (cart.goods?.status == 2) {
        message = "该商品已下架！"
    } else if (cart.goods?.isDelete == 1) {
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
                model = NetworkModule.IMAGE_URL + cart.goods?.image,
                modifier = Modifier.size(100.dp),
                contentDescription = null,
                failure = placeholder(R.drawable.test_image)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(start = 5.dp, end = 15.dp)
            ) {
                Text(
                    text = "${cart.goods?.brand?.bname}${cart.goods?.gname}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Text(
                    text = "$message",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                if (enabled) {
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (price, count) = createRefs()
                        Text(
                            modifier = Modifier.constrainAs(price) {
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                            },
                            text = "￥${NumberUtil.mul(cart.goods?.price, cart.count).setScale(2)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PriceColor
                        )
                        CartCountButton(
                            modifier = Modifier.constrainAs(count) {
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            },
                            count = cart.count ?: 0,
                            onAddClick = onAddClick,
                            onSubClick = onSubClick
                        )
                    }
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
    val colors = SegmentedButtonDefaults.colors(
        activeContainerColor = Color.White,
        inactiveContainerColor = Color.White,
        disabledInactiveContainerColor = Color.White
    )
    MultiChoiceSegmentedButtonRow(
        modifier = modifier
            .height(35.dp)
            .width(120.dp)
    ) {
        SegmentedButton(
            checked = false,
            shape = SegmentedButtonDefaults.itemShape(0, 3),
            colors = colors,
            onCheckedChange = { onSubClick() }
        ) {
            Icon(Icons.Outlined.Remove, null)
        }
        SegmentedButton(
            checked = false,
            enabled = false,
            shape = SegmentedButtonDefaults.itemShape(1, 3),
            colors = colors,
            onCheckedChange = {}
        ) {
            Text("$count")
        }
        SegmentedButton(
            checked = false,
            shape = SegmentedButtonDefaults.itemShape(2, 3),
            colors = colors,
            onCheckedChange = { onAddClick() }
        ) {
            Icon(Icons.Outlined.Add, null)
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