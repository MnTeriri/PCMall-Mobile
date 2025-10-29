package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.pcmallcompose.ui.theme.PriceColor
import com.example.pcmallcompose.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    jumpToDetail: (goods: Goods) -> Unit = {}
) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    Scaffold(
        topBar = { SearchBarView() },
    ) { innerPadding ->
        GoodsListView(
            modifier = Modifier.padding(innerPadding),
            flowData = homeViewModel.getGoodsPagingData(),
            jumpToDetail = jumpToDetail
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarView() {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors = SearchBarDefaults.appBarWithSearchColors()

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clearAndSetSemantics {},
                            text = "搜索商品",
                        )
                    }
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
                trailingIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null)
                    }
                },
            )
        }

    AppBarWithSearch(
        scrollBehavior = scrollBehavior,
        state = searchBarState,
        inputField = inputField,
        colors = appBarWithSearchColors,
        contentPadding = PaddingValues(horizontal = 20.dp),
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            repeat(4) { idx ->
                val resultText = "Suggestion $idx"
                ListItem(
                    headlineContent = { Text(resultText) },
                    supportingContent = { Text("Additional info") },
                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsCarouselContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val pagerState = rememberPagerState(pageCount = { 10 })
        val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()

        val pageInteractionSource = remember { MutableInteractionSource() }
        val pageIsPressed by pageInteractionSource.collectIsPressedAsState()

        val autoAdvance = !pagerIsDragged && !pageIsPressed

        if (autoAdvance) {
            LaunchedEffect(pagerState, pageInteractionSource) {
                while (true) {
                    delay(5000)
                    val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                    pagerState.animateScrollToPage(
                        nextPage,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2
        ) { page ->
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(R.drawable.test_image),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 3.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoodsListView(
    modifier: Modifier = Modifier,
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