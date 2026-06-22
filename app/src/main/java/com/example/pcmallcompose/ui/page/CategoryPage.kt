package com.example.pcmallcompose.ui.page

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.core.model.Brand
import com.example.pcmallcompose.core.model.Category
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.theme.BackgroundColor
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPage() {
    val viewModel: CategoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uiState.categories.size }
    )

    // 左 → 右：点击分类 → 动画跳转 Pager
    var pendingJump by remember { mutableIntStateOf(0) }
    LaunchedEffect(pendingJump) {
        if (pendingJump in uiState.categories.indices) {
            pagerState.animateScrollToPage(pendingJump)
        }
    }

    // 右 → 左：Pager 滑动 → 加载品牌
    LaunchedEffect(pagerState.currentPage) {
        viewModel.loadBrands(pagerState.currentPage)
    }

    Scaffold(
        topBar = { CategoryPageTopBar() }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 左侧 20%：分类列表
            CategoryListPanel(
                modifier = Modifier.weight(0.2f),
                categories = uiState.categories,
                selectedIndex = pagerState.currentPage,
                onSelect = { pendingJump = it },
            )

            // 右侧 80%：品牌网格
            BrandGridPanel(
                modifier = Modifier.weight(0.8f),
                pagerState = pagerState,
                brandsMap = uiState.brandsMap,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPageTopBar() {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(text = "分类") }
    )
}

@Composable
private fun CategoryListPanel(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val itemHeight = 56.dp
    val indicatorOffset by animateDpAsState(
        targetValue = itemHeight * selectedIndex,
        animationSpec = tween(durationMillis = 250)
    )

    Box(
        modifier = modifier.background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(categories) { index, cat ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.cname,
                        fontSize = 14.sp,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                        color = if (index == selectedIndex) MaterialTheme.colorScheme.primary
                        else Color.Black
                    )
                }
            }
        }

        // 选中指示器 — 左侧 3dp 宽竖条
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(itemHeight)
                .offset(y = indicatorOffset)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun BrandGridPanel(
    modifier: Modifier,
    pagerState: PagerState,
    brandsMap: Map<Int, List<Brand>>,
) {
    VerticalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val brands = brandsMap[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            if (brands == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = brands,
                        key = { it.id }
                    ) { brand ->
                        BrandItem(brand)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun BrandItem(brand: Brand) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlideImage(
                model = NetworkModule.IMAGE_URL + brand.image,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                failure = placeholder(R.drawable.test_image)
            )
            Text(
                text = brand.bname,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingCategoryPagePreview() {
    PCMallComposeTheme {
        CategoryPage()
    }
}