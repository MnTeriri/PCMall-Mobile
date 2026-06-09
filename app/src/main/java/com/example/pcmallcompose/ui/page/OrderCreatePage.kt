package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.ErrorMessage
import com.example.pcmallcompose.ui.component.InfoRow
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.ui.theme.PriceColor
import com.example.pcmallcompose.viewmodel.OrderCreateViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCreatePage(
    onBackClick: () -> Unit = {},
    onAddAddressClick: () -> Unit = {},
    onUpdateAddressClick: (Address) -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: OrderCreateViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val totalCount by remember { derivedStateOf { uiState.selectCarts.size } }
    val totalPrice by remember {
        derivedStateOf {
            var total = BigDecimal.ZERO
            uiState.selectCarts.forEach {
                val price = it.goods.price
                total += price.multiply(BigDecimal(it.count))
            }
            total
        }
    }

    var showAddressPicker by rememberSaveable { mutableStateOf(false) }

    // 首次加载
    LaunchedEffect(Unit) {
        viewModel.searchDefaultAddress()
        viewModel.searchSelectCart()
    }

    // 地址选择器关闭 → 重拉默认地址
    LaunchedEffect(showAddressPicker) {
        if (!showAddressPicker) {
            viewModel.searchDefaultAddress()
        }
    }

    // 下单成功
    LaunchedEffect(uiState.isOrderCreated) {
        if (uiState.isOrderCreated) {
            MessageDialog(context, SweetAlertDialog.SUCCESS_TYPE, "下单成功！") {
                onBackClick()
            }.show()
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
        topBar = { OrderCreatePageTopBar(onBackClick) },
        bottomBar = {
            OrderCreatePageBottomBar(
                totalCount = totalCount,
                totalPrice = totalPrice,
                onCreateClick = { viewModel.createOrder() }
            )
        },
    ) { innerPadding ->
        OrderCreateContent(
            modifier = Modifier.padding(innerPadding),
            defaultAddress = uiState.defaultAddress,
            selectCarts = uiState.selectCarts,
            totalCount = totalCount,
            totalPrice = totalPrice,
            onAddressCardClick = { showAddressPicker = true }
        )
    }

    if (showAddressPicker) {
        // 设置直接全屏展开
        val scope = rememberCoroutineScope()
        var skipPartiallyExpanded by remember { mutableStateOf(true) }
        val bottomSheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues =
                if (skipPartiallyExpanded) setOf(SheetValue.Hidden, SheetValue.Expanded)
                else setOf(SheetValue.Hidden, SheetValue.PartiallyExpanded, SheetValue.Expanded),
        )
        ModalBottomSheet(
            onDismissRequest = { showAddressPicker = false },
            sheetState = bottomSheetState
        ) {
            AddressPage(
                isEmbedded = true,
                onBackClick = { showAddressPicker = false },
                onAddAddressClick = {
                    scope.launch { bottomSheetState.hide() }
                    onAddAddressClick()
                },
                onUpdateAddressClick = {
                    scope.launch { bottomSheetState.hide() }
                    onUpdateAddressClick(it)
                }
            )
        }
    }
}

@Composable
private fun OrderCreatePageTopBar(
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text("确认订单") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun OrderCreatePageBottomBar(
    totalCount: Int,
    totalPrice: BigDecimal,
    onCreateClick: () -> Unit,
) {
    BottomAppBar(
        containerColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧 — 汇总信息
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "共", fontSize = 15.sp, color = Color.Black)
                Text(text = "$totalCount", fontSize = 15.sp, color = Color.Black)
                Text(text = "件，合计：", fontSize = 15.sp, color = Color.Black)
                Text(text = "¥${totalPrice.setScale(2)}", fontSize = 15.sp, color = PriceColor)
            }

            // 右侧 — 结算按钮
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107)
                )
            ) {
                Text("结算", color = Color.Black)
            }
        }
    }
}

@Composable
private fun OrderCreateContent(
    modifier: Modifier = Modifier,
    defaultAddress: Address?,
    selectCarts: List<Cart>,
    totalCount: Int,
    totalPrice: BigDecimal,
    onAddressCardClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AddressCard(defaultAddress, onAddressCardClick)
        CartListCard(selectCarts)
        PriceSummaryCard(totalCount, totalPrice)
    }
}

@Composable
private fun AddressCard(
    defaultAddress: Address?,
    onAddressCardClick: () -> Unit,
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
                .clickable(onClick = onAddressCardClick)
                .padding(horizontal = 15.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (defaultAddress == null) {
                // 未选择地址
                Text(
                    modifier = Modifier.weight(1f),
                    text = "未选择地址",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // 地区
                    Text(
                        text = "${defaultAddress.province}${defaultAddress.city}${defaultAddress.district}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 详细地址
                    Text(
                        text = defaultAddress.addressDetail,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // 收件人
                    Text(
                        text = "${defaultAddress.receiverName} ${defaultAddress.phone}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun CartListCard(cartList: List<Cart>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            cartList.forEachIndexed { index, cart ->
                if (index > 0) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
                OrderCreateCartItemView(cart)
            }
        }
    }
}

@Composable
private fun PriceSummaryCard(totalCount: Int, totalPrice: BigDecimal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
        ) {
            InfoRow(label = "商品总价", value = "¥ ${totalPrice.setScale(2)}", valueColor = PriceColor)
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
            InfoRow(label = "商品总数", value = "$totalCount 件")
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
            InfoRow(label = "运费", value = "包邮")
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun OrderCreateCartItemView(cart: Cart) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val goods = cart.goods
        GlideImage(
            model = "${NetworkModule.IMAGE_URL}${goods.image}",
            modifier = Modifier.size(90.dp),
            contentDescription = null,
            failure = placeholder(R.drawable.test_image)
        )

        Spacer(Modifier.width(10.dp))

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

        Spacer(Modifier.width(10.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "¥${goods.price.setScale(2)}",
                fontSize = 13.sp,
                color = PriceColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "×${cart.count}",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

