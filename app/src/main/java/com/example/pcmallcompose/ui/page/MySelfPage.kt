package com.example.pcmallcompose.ui.page

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.application.LocalUserData
import com.example.pcmallcompose.core.model.User
import com.example.pcmallcompose.core.network.di.NetworkModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySelfPage(
    onLoginClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onLoginClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 用户信息
            item(key = "user_info") {
                UserInformationCard(onLoginClick)
            }

            item(key = "order") {
                OrderInformationCard()
            }

            // 服务
            item(key = "service") {
                ServiceCard(
                    onAddressClick = onAddressClick
                )
            }
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
    onLoginClick: () -> Unit={}
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
private fun OrderInformationCard() {
    val context = LocalContext.current
    val user = LocalUserData.current

    val items = listOf(
        mapOf("label" to "待付款", "icon" to R.drawable.ic_order_pay),
        mapOf("label" to "待发货", "icon" to R.drawable.ic_order_send),
        mapOf("label" to "待收货", "icon" to R.drawable.ic_order_deliver),
        mapOf("label" to "已完成", "icon" to R.drawable.ic_order_finish),
        mapOf("label" to "售后服务", "icon" to R.drawable.ic_order_refund),
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("我的订单", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("全部", fontSize = 13.sp, color = Color.Gray)
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 订单入口
            NavigationBar(containerColor = Color.White) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(item["icon"] as Int),
                                contentDescription = null
                            )
                        },
                        label = { Text(text = "${item["label"]}", fontSize = 12.sp) },
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
    data class ServiceItem(
        val label: String,
        val iconRes: Int,
        val onClick: () -> Unit,
    )

    val items = listOf(
        ServiceItem("AI导购", R.drawable.ic_order_pay, onAiClick),
        ServiceItem("地址管理", R.drawable.ic_order_pay, onAddressClick),
        ServiceItem("购物车", R.drawable.ic_order_pay, onCartClick),
        ServiceItem("修改密码", R.drawable.ic_order_pay, onPasswordClick),
        ServiceItem("售后服务", R.drawable.ic_order_pay, onServiceClick),
        ServiceItem("退出登录", R.drawable.ic_order_pay, onLogoutClick)
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
