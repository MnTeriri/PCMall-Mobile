package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.theme.BackgroundColor
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.ui.theme.PriceColor
import kotlinx.coroutines.launch

@Composable
fun GoodsDetailPage(goods: Goods) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = { GoodsDetailTopBar(listState) },
        bottomBar = { GoodsDetailBottomBar() }
    ) { paddingValues ->
        GoodsDetailContent(
            modifier = Modifier.padding(paddingValues),
            listState = listState,
            goods = goods
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoodsDetailTopBar(listState: LazyListState) {
    val coroutineScope = rememberCoroutineScope()
    val state by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    TopAppBar(
        colors = topAppBarColors(containerColor = Color.White),
        title = {
            val titles = listOf("商品", "评价", "详情", "推荐")
            PrimaryTabRow(
                containerColor = Color.White,
                contentColor = Color.Black,
                selectedTabIndex = state,
                divider = {}
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { coroutineScope.launch { listState.animateScrollToItem(index) } },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Add to favorites",
                )
            }
        },
    )
}

@Composable
fun GoodsDetailBottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.height(50.dp),
            shape = RectangleShape,
            onClick = {}
        ) {
            Column {
                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = null
                )
                Text(text = "收藏", fontSize = 12.sp)
            }
        }
        IconButton(
            modifier = Modifier.height(50.dp),
            shape = RectangleShape,
            onClick = {}
        ) {
            Column {
                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_bottom_cart),
                    contentDescription = null
                )
                Text(text = "购物车", fontSize = 12.sp)
            }
        }
        Button(
            modifier = Modifier.width(120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray
            ),
            onClick = {}
        ) {
            Text("加入购物车")
        }
        Button(
            modifier = Modifier.width(120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            onClick = {}
        ) {
            Text("立即购买")
        }
    }
}

@Composable
fun GoodsDetailContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    goods: Goods
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { GoodsInformationItem(goods) }
        item { GoodsCommitItem() }
        item { GoodsDetailItem() }
        item { RecommendItem() }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GoodsInformationItem(goods: Goods) {
    GlideImage(
        model = NetworkModule.IMAGE_URL + goods.image,
        modifier = Modifier.fillMaxWidth(),
        contentDescription = null,
        failure = placeholder(R.drawable.test_image),
        contentScale = ContentScale.FillWidth
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
            .padding(horizontal = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "${goods.brand?.bname} ${goods.gname}",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                modifier = Modifier.padding(top = 3.dp),
                text = "${goods.description}",
                fontSize = 16.sp,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            Text(
                modifier = Modifier.padding(top = 3.dp),
                text = "￥${goods.price?.setScale(2)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PriceColor
            )
        }

    }
}

@Composable
fun GoodsCommitItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
            .padding(horizontal = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "评价",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            repeat(20) { index ->
                Text("$index")
            }
        }
    }
}

@Composable
fun GoodsDetailItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
            .padding(horizontal = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "详情",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            repeat(30) { index ->
                Text("$index")
            }
        }
    }
}

@Composable
fun RecommendItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
            .padding(horizontal = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "推荐",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            repeat(30) { index ->
                Text("$index")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GoodsDetailPreview() {
    PCMallComposeTheme {

    }
}