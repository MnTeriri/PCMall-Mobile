package com.example.pcmallcompose.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.LocalDateTimeUtil
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pcmallcompose.core.model.ChatHistory
import com.example.pcmallcompose.core.model.ChatHistory.ChatHistoryType
import com.example.pcmallcompose.core.model.ChatHistory.ChatStatus
import com.example.pcmallcompose.core.model.Goods
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.theme.AssistantMessageColor
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.ui.theme.UserMessageColor
import com.example.pcmallcompose.viewmodel.AiChatViewModel
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.rememberMarkdownState
import kotlinx.coroutines.flow.Flow

@Composable
fun AiChatPage(
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    val viewModel: AiChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AiChatPageTopBar(
                onBackClick = onBackClick,
                onCloseClick = onCloseClick
            )
        },
        bottomBar = {
            AiChatPageInputBar(
                isChatting = uiState.isChatting,
                onChatClick = { viewModel.chat(it) }
            )
        }
    ) { innerPadding ->
        AiChatHistoryListView(
            modifier = Modifier.padding(innerPadding),
            streamingContent = uiState.streamingContent,
            pagingFlow = viewModel.chatHistoryPagingFlow
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatPageTopBar(
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null
                )
            }
        },
        title = { Text("AI导购") }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiChatPageInputBar(
    isChatting: Boolean,
    onChatClick: (String) -> Unit = {},
    onDismissClick: () -> Unit = {}
) {
    BottomAppBar {
        var text by remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(15.dp),
                value = text,
                onValueChange = { text = it },
                label = { Text(text = "发消息...") },
                singleLine = true,
                enabled = !isChatting
            )
            Button(
                modifier = Modifier.size(80.dp, 50.dp),
                onClick = {
                    onChatClick(text)
                    text = ""
                },
                enabled = !isChatting
            ) {

                AnimatedContent(
                    targetState = isChatting,
                ) { targetState ->
                    when (targetState) {
                        true -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = Color.White
                            )
                        }
                        false -> {
                            Text("发送")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiChatHistoryListView(
    modifier: Modifier = Modifier,
    streamingContent: String,
    pagingFlow: Flow<PagingData<ChatHistory>>,
) {
    val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        reverseLayout = true
    ) {
        items(
            count = lazyPagingItems.itemCount
        ) {
            val history = lazyPagingItems[it]!!
            if (history.type == ChatHistoryType.AI && history.chatStatus == ChatStatus.CHATTING) {
                // 占位行：读取 streamingContent 显示
                AiChatStreamingItemView(streamingContent)
            } else {
                AiChatHistoryItemView(history)
            }
        }
    }
}

@Composable
fun AiChatHistoryItemView(history: ChatHistory) {
    val columnPaddingValues = when (history.type) {
        ChatHistoryType.AI -> PaddingValues(end = 20.dp, bottom = 20.dp)
        ChatHistoryType.USER -> PaddingValues(start = 20.dp, bottom = 20.dp)
    }

    val horizontalAlignment = when (history.type) {
        ChatHistoryType.AI -> Alignment.Start
        ChatHistoryType.USER -> Alignment.End
    }

    val backgroundColor = when (history.type) {
        ChatHistoryType.AI -> AssistantMessageColor
        ChatHistoryType.USER -> UserMessageColor
    }

    val timeTextPaddingValues = when (history.type) {
        ChatHistoryType.AI -> PaddingValues(start = 5.dp, bottom = 3.dp)
        ChatHistoryType.USER -> PaddingValues(end = 5.dp, bottom = 3.dp)
    }

    val contentTextColor = when (history.type) {
        ChatHistoryType.AI -> Color.Black
        ChatHistoryType.USER -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(columnPaddingValues),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            modifier = Modifier.padding(timeTextPaddingValues),
            fontSize = 12.sp,
            color = Color.Black,
            text = LocalDateTimeUtil.format(history.createTime, DatePattern.NORM_DATETIME_FORMATTER)
        )
        if (history.type == ChatHistoryType.AI) {
            if (history.chatStatus == ChatStatus.ERROR) {
                // 错误气泡：红色退底，图标 + 文案
                Row(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFFCE4EC),   // 浅红
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "生成失败，请稍后重试",
                        fontSize = 14.sp,
                        color = Color(0xFFD32F2F)
                    )
                }
            } else {
                val markdownState = rememberMarkdownState(
                    content = history.content.trimIndent(),
                    immediate = true
                )

                Markdown(
                    markdownState = markdownState,
                    modifier = Modifier
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    colors = markdownColor(),
                    typography = markdownTypography(
                        h1 = MaterialTheme.typography.displaySmall,
                        h2 = MaterialTheme.typography.headlineMedium,
                        h3 = MaterialTheme.typography.headlineSmall,
                        h4 = MaterialTheme.typography.titleLarge,
                        h5 = MaterialTheme.typography.titleMedium,
                        h6 = MaterialTheme.typography.titleSmall,
                    )
                )

                // ── 推荐商品（仅 AI 消息有内容时显示）──
                if (history.recommends.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    RecommendGoodsStrip(history.recommends)
                }
            }
        } else {
            Text(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                fontSize = 16.sp,
                lineHeight = 21.sp,
                color = contentTextColor,
                text = history.content
            )
        }
    }
}

@Composable
fun AiChatStreamingItemView(content: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val markdownState = rememberMarkdownState(
            content = content,
            retainState = true,
        )
        Markdown(
            markdownState = markdownState,
            modifier = Modifier
                .background(
                    color = AssistantMessageColor,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 15.dp, vertical = 10.dp),
            colors = markdownColor(),
            typography = markdownTypography(
                h1 = MaterialTheme.typography.displaySmall,
                h2 = MaterialTheme.typography.headlineMedium,
                h3 = MaterialTheme.typography.headlineSmall,
                h4 = MaterialTheme.typography.titleLarge,
                h5 = MaterialTheme.typography.titleMedium,
                h6 = MaterialTheme.typography.titleSmall,
            )
        )
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 2.dp)
        ) {
            // 右下角闪烁光标提示
            Text(
                modifier = Modifier.alpha(cursorAlpha),
                fontSize = 12.sp,
                color = Color.Gray,
                text = "●"
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                fontSize = 12.sp,
                color = Color.Gray,
                text = "正在输出..."
            )
        }
    }
}

@Composable
private fun RecommendGoodsStrip(goodsList: List<Goods>) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // 折叠/展开按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AssistantMessageColor,
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Recommend,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "为您推荐 ${goodsList.size} 件商品",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp
                else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }

        // 展开内容：水平滚动的商品卡片
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Spacer(Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = goodsList,
                        key = { it.id }
                    ) { goods ->
                        RecommendGoodsItem(goods)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RecommendGoodsItem(goods: Goods) {
    ElevatedCard(
        modifier = Modifier.size(width = 120.dp, height = 140.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                model = "${NetworkModule.IMAGE_URL}${goods.image}",
                contentDescription = null
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = goods.gname,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 1,
                color = Color.Black
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "¥${goods.price}",
                fontSize = 14.sp,
                color = Color(0xFFE53935),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingChatGLMPagePreview() {
    PCMallComposeTheme {
        AiChatPage()
    }
}