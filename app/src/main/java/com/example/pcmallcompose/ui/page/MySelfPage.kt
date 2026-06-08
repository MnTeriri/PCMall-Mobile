package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.application.LocalUserData
import com.example.pcmallcompose.core.model.User
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.ErrorMessage
import com.example.pcmallcompose.ui.Screen
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.viewmodel.MySelfViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySelfPage(
    onLoginClick: () -> Unit = {},
    onOrderClick: (Screen.Order.OrderTab) -> Unit = {},
    onAddressClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val user = LocalUserData.current
    val viewModel: MySelfViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 登录状态变化时重新拉取订单数量
    LaunchedEffect(user) {
        viewModel.fetchOrderCounts()
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
        floatingActionButton = {
            FloatingActionButton(onClick = onLoginClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 用户信息
            UserInformationCard(onLoginClick)

            //订单卡片
            OrderInformationCard(
                orderCounts = uiState.orderCounts,
                onOrderClick = onOrderClick
            )

            // 服务卡片
            ServiceCard(
                onAddressClick = onAddressClick
            )
        }
    }
}

@Composable
private fun UserInformationCard(
    onLoginClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        val user = LocalUserData.current
        if (user == null) {
            LoginButton(onLoginClick)
        } else {
            UserInformation(user)
        }
    }
}

@Composable
private fun LoginButton(
    onLoginClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(25.dp))
        Image(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(R.drawable.test_image),
            contentDescription = null
        )
        Spacer(Modifier.width(10.dp))
        TextButton(
            onClick = onLoginClick,
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
        ) {
            Text(text = "登录/注册", fontSize = 20.sp)
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun UserInformation(user: User) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(25.dp))
        GlideImage(
            model = NetworkModule.IMAGE_URL + user.image,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            failure = placeholder(R.drawable.test_image),
            contentDescription = null
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.padding(start = 5.dp)) {
            Text(
                text = user.uname,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = "UID：${user.uid}",
                color = Color.Gray,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun OrderInformationCard(
    orderCounts: Map<Screen.Order.OrderTab, Long> = emptyMap(),
    onOrderClick: (Screen.Order.OrderTab) -> Unit
) {
    data class OrderNavBarItem(
        val tab: Screen.Order.OrderTab,
        val iconRes: Int,
    )

    val items = listOf(
        OrderNavBarItem(Screen.Order.OrderTab.PENDING_PAYMENT, R.drawable.ic_order_pay),
        OrderNavBarItem(Screen.Order.OrderTab.PENDING_SHIPMENT, R.drawable.ic_order_send),
        OrderNavBarItem(Screen.Order.OrderTab.PENDING_RECEIPT, R.drawable.ic_order_deliver),
        OrderNavBarItem(Screen.Order.OrderTab.SUCCESS, R.drawable.ic_order_finish),
        OrderNavBarItem(Screen.Order.OrderTab.RETURNING, R.drawable.ic_order_refund),
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "我的订单", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.clickable {
                        onOrderClick(Screen.Order.OrderTab.ALL)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "全部", fontSize = 13.sp, color = Color.Gray)
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 订单入口
            NavigationBar(containerColor = Color.White) {
                items.forEach { item ->
                    val count = orderCounts[item.tab] ?: 0L
                    NavigationBarItem(
                        selected = false,
                        onClick = { onOrderClick(item.tab) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (count > 0) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        ) {
                                            Text("$count")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(item.iconRes),
                                    contentDescription = null
                                )
                            }
                        },
                        label = { Text(text = item.tab.label, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    onAiClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onPasswordClick: () -> Unit = {},
    onServiceClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    data class ServiceNavBarItem(
        val label: String,
        val iconRes: Int,
        val onClick: () -> Unit,
    )

    val items = listOf(
        ServiceNavBarItem("AI导购", R.drawable.ic_order_pay, onAiClick),
        ServiceNavBarItem("地址管理", R.drawable.ic_order_pay, onAddressClick),
        ServiceNavBarItem("购物车", R.drawable.ic_order_pay, onCartClick),
        ServiceNavBarItem("修改密码", R.drawable.ic_order_pay, onPasswordClick),
        ServiceNavBarItem("售后服务", R.drawable.ic_order_pay, onServiceClick),
        ServiceNavBarItem("退出登录", R.drawable.ic_order_pay, onLogoutClick)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("服务", fontSize = 15.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(4.dp))

            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            item.onClick()
                        }
                        .padding(vertical = 14.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ImageVector.vectorResource(item.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = Color.DarkGray
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(text = item.label, fontSize = 15.sp)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
