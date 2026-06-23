package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.pcmallcompose.application.LocalUserData
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.ui.ErrorMessage
import com.example.pcmallcompose.ui.Screen
import com.example.pcmallcompose.ui.component.LoginPrompt
import com.example.pcmallcompose.ui.component.OrderActionButtons
import com.example.pcmallcompose.ui.component.OrderGoodsItemView
import com.example.pcmallcompose.ui.dialog.MessageDialog
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
    onOrderClick: (Order) -> Unit = {},
) {
    val userData = LocalUserData.current
    val context = LocalContext.current
    val viewModel: OrderViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 操作成功
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            MessageDialog(context, SweetAlertDialog.SUCCESS_TYPE, "操作成功").show()
            viewModel.successConsumed()
        }
    }

    // 错误消息
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
        topBar = {
            OrderPageSearchBar(
                onBackClick = onBackClick,
                onAiClick = onAiClick,
            )
        }
    ) { innerPadding ->
        if (userData == null) {
            LoginPrompt(text = "登录后使用订单管理", onLoginClick = {})
        } else {
            OrderPageContent(
                modifier = Modifier.padding(innerPadding),
                selectTab = selectTab,
                pagingDataFactory = { viewModel.getOrderPagingData(it) },
                onOrderClick = onOrderClick,
                isLoading = uiState.isLoading,
                onPayClick = { viewModel.payOrder(it) },
                onSuccessClick = { viewModel.finishOrder(it) },
                onCancelClick = { viewModel.cancelOrder(it) },
                onRefundClick = { viewModel.refundOrder(it) },
            )
        }
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
    onOrderClick: (Order) -> Unit,
    isLoading: Boolean,
    onPayClick: (String) -> Unit,
    onSuccessClick: (String) -> Unit,
    onCancelClick: (String) -> Unit,
    onRefundClick: (String) -> Unit,
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
                lazyPagingItems = lazyPagingItems,
                onOrderClick = onOrderClick,
                isLoading = isLoading,
                onPayClick = onPayClick,
                onSuccessClick = onSuccessClick,
                onCancelClick = onCancelClick,
                onRefundClick = onRefundClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OrderListView(
    lazyPagingItems: LazyPagingItems<Order>,
    onOrderClick: (Order) -> Unit,
    isLoading: Boolean,
    onPayClick: (String) -> Unit,
    onSuccessClick: (String) -> Unit,
    onCancelClick: (String) -> Unit,
    onRefundClick: (String) -> Unit,
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
                    isLoading = isLoading,
                    onOrderClick = { onOrderClick(order) },
                    onPayClick = { onPayClick(order.oid) },
                    onSuccessClick = { onSuccessClick(order.oid) },
                    onCancelClick = { onCancelClick(order.oid) },
                    onRefundClick = { onRefundClick(order.oid) },
                )
            }
        }
    }
}

@Composable
private fun OrderItemView(
    order: Order,
    isLoading: Boolean,
    onOrderClick: () -> Unit,
    onPayClick: () -> Unit,
    onSuccessClick: () -> Unit,
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
                OrderGoodsItemView(goods)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                status = order.status,
                isLoading = isLoading,
                onPayClick = onPayClick,
                onSuccessClick = onSuccessClick,
                onCancelClick = onCancelClick,
                onRefundClick = onRefundClick,
            )
        }
    }
}