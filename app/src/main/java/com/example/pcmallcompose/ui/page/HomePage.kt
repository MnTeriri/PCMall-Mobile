package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.module.NetworkModule
import com.example.pcmallcompose.ui.theme.BackgroundColor
import com.example.pcmallcompose.ui.theme.PriceColor
import com.example.pcmallcompose.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    jumpToDetail: (goods: Goods) -> Unit = {}
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .semantics { isTraversalGroup = true }
    ) {
        SearchBarView(modifier = Modifier.align(Alignment.TopCenter))
        GoodsListView(homeViewModel.getGoodsPagingData(), jumpToDetail)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarView(modifier: Modifier = Modifier) {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        modifier = modifier.semantics { traversalIndex = 0f },
        inputField = {
            SearchBarDefaults.InputField(
                query = text,
                onQueryChange = { text = it },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("搜索商品") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            repeat(4) { idx ->
                val resultText = "Suggestion $idx"
                ListItem(
                    headlineContent = { Text(resultText) },
                    supportingContent = { Text("Additional info") },
                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier =
                    Modifier
                        .clickable {
                            text = resultText
                            expanded = false
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsCarouselContent() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int
    )

    val items = listOf(
        CarouselItem(0, R.drawable.test_image),
        CarouselItem(1, R.drawable.test_image),
        CarouselItem(2, R.drawable.test_image),
        CarouselItem(3, R.drawable.test_image),
        CarouselItem(4, R.drawable.test_image),
    )

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .height(221.dp),
        preferredItemWidth = 500.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .fillMaxWidth()
                .maskClip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsListView(
    flowData: Flow<PagingData<Goods>>,
    jumpToDetail: (goods: Goods) -> Unit = {}
) {
    val context = LocalContext.current
    val pager = remember { flowData }
    val lazyPagingItems = pager.collectAsLazyPagingItems()

    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }//是否在刷新
    val state = rememberPullToRefreshState()//下拉刷新的状态管理

    val onRefresh: () -> Unit = {
        coroutineScope.launch {
            isRefreshing = true//设置正在刷新
            lazyPagingItems.refresh()
            delay(1000)
            isRefreshing = false //刷新业务执行完毕后修改状态
        }
    }

    PullToRefreshBox(
        modifier = Modifier.padding(top = 104.dp),
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                GoodsCarouselContent()
            }

            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id!! }
            ) { index ->
                GoodsItemView(lazyPagingItems[index]!!, jumpToDetail)
            }
        }
    }

    if (lazyPagingItems.loadState.hasError) {
        Toast.makeText(context, "错误！", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GoodsItemView(
    goods: Goods,
    jumpToDetail: (goods: Goods) -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .clickable { jumpToDetail(goods) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlideImage(
                model = NetworkModule.IMAGE_URL + goods.image,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = null,
                failure = placeholder(R.drawable.test_image),
                contentScale = ContentScale.FillWidth
            )

            ConstraintLayout(
                modifier = Modifier
                    .height(90.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                val (name, description, price) = createRefs()
                Text(
                    modifier = Modifier.constrainAs(name) {
                        top.linkTo(parent.top)
                    },
                    text = "${goods.brand?.bname} ${goods.gname}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.constrainAs(description) {
                        start.linkTo(parent.start)
                        top.linkTo(name.bottom)
                    },
                    text = "${goods.description}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.constrainAs(price) {
                        bottom.linkTo(parent.bottom)
                    },
                    text = "￥${goods.price?.setScale(2)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PriceColor
                )
            }
        }
    }
}