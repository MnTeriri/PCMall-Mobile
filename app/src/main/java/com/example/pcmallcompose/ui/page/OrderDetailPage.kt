package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.LocalDateTimeUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.core.model.OrderAddress
import com.example.pcmallcompose.core.model.OrderGoods
import com.example.pcmallcompose.ui.ErrorMessage
import com.example.pcmallcompose.ui.component.InfoRow
import com.example.pcmallcompose.ui.component.OrderActionButtons
import com.example.pcmallcompose.ui.component.OrderGoodsItemView
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.ui.theme.PriceColor
import com.example.pcmallcompose.viewmodel.OrderDetailViewModel

@Composable
fun OrderDetailPage(
    order: Order,
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: OrderDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 操作成功 → 弹窗后返回
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            MessageDialog(context, SweetAlertDialog.SUCCESS_TYPE, "操作成功") {
                onBackClick()
            }.show()
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
        topBar = { OrderDetailTopBar(onBackClick) },
        bottomBar = {
            OrderDetailBottomBar(
                order = order,
                isLoading = uiState.isLoading,
                onPayClick = { viewModel.payOrder(order.oid) },
                onSuccessClick = { viewModel.finishOrder(order.oid) },
                onRefundClick = { viewModel.refundOrder(order.oid) },
                onCancelClick = { viewModel.cancelOrder(order.oid) },
            )
        }
    ) { paddingValues ->
        OrderDetailContent(
            modifier = Modifier.padding(paddingValues),
            order = order,
        )
    }
}

@Composable
private fun OrderDetailTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text("订单详情") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun OrderDetailBottomBar(
    order: Order,
    isLoading: Boolean,
    onPayClick: () -> Unit,
    onSuccessClick: () -> Unit,
    onRefundClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    BottomAppBar(
        containerColor = Color.White
    ) {
        OrderActionButtons(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .padding(horizontal = 16.dp),
            status = order.status,
            isLoading = isLoading,
            onPayClick = onPayClick,
            onSuccessClick = onSuccessClick,
            onCancelClick = onCancelClick,
            onRefundClick = onRefundClick,
        )
    }
}

@Composable
private fun OrderDetailContent(
    modifier: Modifier = Modifier,
    order: Order,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AddressCard(order.address)
        GoodsListCard(order.goodsList)
        OrderInfoCard(order)
    }
}

@Composable
private fun AddressCard(address: OrderAddress) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 18.dp)
        ) {
            Text(
                text = "${address.province}${address.city}${address.district}",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = address.addressDetail,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "${address.receiverName} ${address.phone}",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun GoodsListCard(goodsList: List<OrderGoods>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
        ) {
            goodsList.forEachIndexed { index, goods ->
                if (index > 0) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
                OrderGoodsItemView(goods)
            }
        }
    }
}

@Composable
private fun OrderInfoCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
        ) {
            InfoRow(label = "实付金额", value = "¥${(order.price).setScale(2)}", valueColor = PriceColor)
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            InfoRow(label = "商品总数", value = "${order.goodsList.size} 件")
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            InfoRow(label = "运费", value = "免运费")
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            InfoRow(label = "订单状态", value = order.status.label, valueColor = PriceColor)
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            InfoRow(label = "订单编号", value = order.oid)
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            InfoRow(label = "下单时间", value = LocalDateTimeUtil.format(order.createTime, DatePattern.NORM_DATETIME_FORMATTER))
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            val payTime = if (order.payTime == null) "------" else LocalDateTimeUtil.format(
                order.payTime,
                DatePattern.NORM_DATETIME_FORMATTER
            )
            InfoRow(label = "付款时间", value = payTime)
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            val sendTime = if (order.sendTime == null) "------" else LocalDateTimeUtil.format(
                order.sendTime,
                DatePattern.NORM_DATETIME_FORMATTER
            )
            InfoRow(label = "发货时间", value = sendTime)
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            val finishTime = if (order.finishTime == null) "------" else LocalDateTimeUtil.format(
                order.finishTime,
                DatePattern.NORM_DATETIME_FORMATTER
            )
            InfoRow(label = "完成时间", value = finishTime)
        }
    }
}