package com.example.pcmallcompose.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.pcmallcompose.core.model.ChatHistory
import com.example.pcmallcompose.core.model.ChatHistory.ChatHistoryType.AI
import com.example.pcmallcompose.core.model.ChatHistory.ChatHistoryType.USER
import com.example.pcmallcompose.ui.theme.AssistantMessageColor
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.ui.theme.UserMessageColor
import com.example.pcmallcompose.viewmodel.AiChatViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun AiChatPage() {
    val aiChatViewModel: AiChatViewModel = hiltViewModel()
    val uiState by aiChatViewModel.uiState.collectAsStateWithLifecycle()

    val pagingFlow = remember { aiChatViewModel.getChatHistoryPagingData() }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { AiChatTopBar() },
        bottomBar = {
            AiChatInputBar(
                isChatting = uiState.isChatting,
                onChatClick = { aiChatViewModel.chat(it) }
            )
        }
    ) { innerPadding ->
        AiChatHistoryListView(
            modifier = Modifier.padding(innerPadding),
            pagingFlow = pagingFlow
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatTopBar(
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
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
        title = { Text("AI助手") }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiChatInputBar(
    isChatting: Boolean,
    onChatClick: (String) -> Unit = {},
    onDismissClick: () -> Unit = {}
) {
    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.Black,
    ) {
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
    pagingFlow: Flow<PagingData<ChatHistory>>
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
            val history = lazyPagingItems[it]
            history?.let {
                AiChatHistoryItemView(history)
            }
        }
    }
}

@Composable
fun AiChatHistoryItemView(history: ChatHistory) {
    val columnPaddingValues = when (history.type) {
        AI -> PaddingValues(end = 20.dp, bottom = 20.dp)
        USER -> PaddingValues(start = 20.dp, bottom = 20.dp)
    }

    val horizontalAlignment = when (history.type) {
        AI -> Alignment.Start
        USER -> Alignment.End
    }

    val backgroundColor = when (history.type) {
        AI -> AssistantMessageColor
        USER -> UserMessageColor
    }

    val timeTextPaddingValues = when (history.type) {
        AI -> PaddingValues(start = 5.dp, bottom = 3.dp)
        USER -> PaddingValues(end = 5.dp, bottom = 3.dp)
    }

    val contentTextColor = when (history.type) {
        AI -> Color.Black
        USER -> Color.White
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

@Preview(showBackground = true)
@Composable
fun GreetingChatGLMPagePreview() {
    PCMallComposeTheme {
        AiChatPage()
    }
}