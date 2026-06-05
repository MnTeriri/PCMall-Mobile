package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberContainedSearchBarState
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.LocalDateTimeUtil
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.core.model.Order.OrderState.CANCELED
import com.example.pcmallcompose.core.model.Order.OrderState.PENDING_PAYMENT
import com.example.pcmallcompose.core.model.Order.OrderState.PENDING_RECEIPT
import com.example.pcmallcompose.core.model.Order.OrderState.PENDING_SHIPMENT
import com.example.pcmallcompose.core.model.Order.OrderState.RETURNED
import com.example.pcmallcompose.core.model.Order.OrderState.RETURNING
import com.example.pcmallcompose.core.model.Order.OrderState.SUCCESS
import com.example.pcmallcompose.core.model.OrderGoods
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.Screen
import com.example.pcmallcompose.ui.theme.PriceColor
import com.example.pcmallcompose.viewmodel.OrderViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun OrderPage(
    selectTab: Screen.Order.OrderTab = Screen.Order.OrderTab.ALL,
    onBackClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
) {
    val viewModel: OrderViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            OrderPageSearchBar(
                onBackClick = onBackClick,
                onAiClick = onAiClick,
            )
        }
    ) { innerPadding ->
        OrderPageContent(
            modifier = Modifier.padding(innerPadding),
            selectTab = selectTab,
            pagingDataFactory = { viewModel.getOrderPagingData(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderPageSearchBar(
    onBackClick: () -> Unit,
    onAiClick: () -> Unit
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(canScroll = { false })
    val appBarWithSearchColors = SearchBarDefaults.appBarWithSearchColors()

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "搜索订单")
                },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {},
            )
        }

    AppBarWithSearch(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
        colors = appBarWithSearchColors,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = onAiClick) {
                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null)
            }
        },
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
        colors = appBarWithSearchColors.searchBarColors,
    ) {

    }
}


@Composable
private fun OrderPageContent(
    modifier: Modifier = Modifier,
    selectTab: Screen.Order.OrderTab,
    pagingDataFactory: (Int) -> Flow<PagingData<Order>>,
) {
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf(
        Screen.Order.OrderTab.ALL,
        Screen.Order.OrderTab.PENDING_PAYMENT,
        Screen.Order.OrderTab.PENDING_SHIPMENT,
        Screen.Order.OrderTab.PENDING_RECEIPT,
        Screen.Order.OrderTab.SUCCESS,
        Screen.Order.OrderTab.CANCELED,
        Screen.Order.OrderTab.RETURNING,
        Screen.Order.OrderTab.RETURNED
    )

    val pagerState = rememberPagerState(
        initialPage = tabs.indexOf(selectTab),
        pageCount = { tabs.size }
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White,
            edgePadding = 10.dp,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = tab.label, fontSize = 14.sp) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val tab = tabs[page]
            val lazyPagingItems = pagingDataFactory(tab.code).collectAsLazyPagingItems()

            LaunchedEffect(Unit) {
                lazyPagingItems.refresh()
            }

            OrderListView(
                lazyPagingItems = lazyPagingItems
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OrderListView(
    lazyPagingItems: LazyPagingItems<Order>,
    onOrderClick: () -> Unit = {},
    onPayClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onRefundClick: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }//是否在刷新
    val state = rememberPullToRefreshState()//下拉刷新的状态管理

    val onRefresh: () -> Unit = {
        coroutineScope.launch {
            isRefreshing = true
            delay(1000.milliseconds)
            lazyPagingItems.refresh()
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
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
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) {
                val order = lazyPagingItems[it]!!
                OrderItemView(
                    order = order,
                    onOrderClick = onOrderClick,
                    onPayClick = onPayClick,
                    onConfirmClick = onConfirmClick,
                    onCancelClick = onCancelClick,
                    onRefundClick = onRefundClick,
                )
            }
        }
    }
}

@Composable
private fun OrderItemView(
    order: Order,
    onOrderClick: () -> Unit,
    onPayClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    onRefundClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clickable { onOrderClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
        ) {
            // ── 订单号 + 状态 ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "订单号: ${order.oid}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = order.status.label,
                    fontSize = 13.sp,
                    color = PriceColor,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                thickness = 0.5.dp,
                color = Color(0xFFEEEEEE)
            )

            // ── 商品列表 ──
            order.goodsList.forEach { goods ->
                OrderGoodsItemView(goods = goods)
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                thickness = 0.5.dp,
                color = Color(0xFFEEEEEE)
            )

            // ── 底部：创建时间 + 件数 / 金额 ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalDateTimeUtil.format(order.createTime, DatePattern.NORM_DATETIME_FORMATTER),
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "共${order.goodsList.size}件  ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "¥${order.price.setScale(2)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PriceColor
                    )
                }
            }

            OrderActionButtons(
                status = order.status,
                onPayClick = onPayClick,
                onCancelClick = onCancelClick,
                onConfirmClick = onConfirmClick,
                onRefundClick = onRefundClick,
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OrderGoodsItemView(goods: OrderGoods) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = "${NetworkModule.IMAGE_URL}${goods.image}",
            modifier = Modifier.size(90.dp),
            contentDescription = null,
            failure = placeholder(R.drawable.test_image)
        )

        Spacer(Modifier.width(6.dp))

        // 中间信息 — weight(1f) 吃掉图片和价格之间的所有剩余空间，不设固定宽度
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${goods.brand.bname} ${goods.gname}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = goods.description,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.width(6.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "¥${goods.price.setScale(2)}",
                fontSize = 13.sp,
                color = PriceColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "×${goods.count}",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// ── 操作按钮（按状态决定显示哪些按钮）──
@Composable
private fun OrderActionButtons(
    status: Order.OrderState,
    onPayClick: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onRefundClick: () -> Unit,
) {
    if (status == CANCELED || status == RETURNING || status == RETURNED) {
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        val buttonModifier = Modifier.size(90.dp, 35.dp)
        val buttonPadding = PaddingValues(horizontal = 10.dp)

        when (status) {
            PENDING_PAYMENT -> {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding
                ) {
                    Text("取消订单", fontSize = 13.sp)
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onPayClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    colors = ButtonDefaults.buttonColors(containerColor = PriceColor)
                ) {
                    Text("去付款", fontSize = 13.sp, color = Color.White)
                }
            }

            PENDING_SHIPMENT -> {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding
                ) {
                    Text("取消订单", fontSize = 13.sp)
                }
            }

            PENDING_RECEIPT -> {
                OutlinedButton(
                    onClick = onRefundClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding
                ) {
                    Text("申请售后", fontSize = 13.sp)
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onConfirmClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    colors = ButtonDefaults.buttonColors(containerColor = PriceColor)
                ) {
                    Text("确认签收", fontSize = 13.sp, color = Color.White)
                }
            }

            SUCCESS -> {
                OutlinedButton(
                    onClick = onRefundClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding
                ) {
                    Text("申请售后", fontSize = 13.sp)
                }
            }
        }
    }
}