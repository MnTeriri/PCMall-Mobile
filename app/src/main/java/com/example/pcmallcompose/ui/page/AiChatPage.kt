package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme

@Composable
fun AiChatPage() {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { AiChatTopBar() },
        bottomBar = { AiChatInputBar() }
    ) { innerPadding ->
//        ChatGLMHistory(innerPadding, listState, chatGLMViewModel)
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


@Composable
fun AiChatInputBar(
    isAiLoading: Boolean = false,
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
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically

        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(bottom = 5.dp),
                shape = RoundedCornerShape(15.dp),
                value = text,
                onValueChange = { text = it },
                label = { Text(text = "发消息...") },
                singleLine = true,
                enabled = !isAiLoading
            )
            Button(
                onClick = {
                    onChatClick(text)
                    text = ""
                },
                enabled = !isAiLoading
            ) {
                Text("发送")
            }
        }
    }
}

//
//@Composable
//fun ChatGLMHistory(
//    innerPadding: PaddingValues,
//    listState: LazyListState,
//    chatGLMViewModel: ChatGLMViewModel
//) {
//    val isLoadMore by remember { derivedStateOf { !listState.lastScrolledBackward && listState.firstVisibleItemIndex % 30 >= 15 } }
//
//    if (isLoadMore) {
//        chatGLMViewModel.loadMoreMessage()
//    }
//
//    LazyColumn(
//        modifier = Modifier
//            .background(BackgroundColor)
//            .padding(innerPadding)
//            .fillMaxSize(),
//        state = listState,
//        contentPadding = PaddingValues(horizontal = 20.dp),
//        reverseLayout = true
//    ) {
//        if (chatGLMViewModel.isLoading) {
//            item {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(end = 20.dp, bottom = 20.dp),
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .background(
//                                color = AssistantMessageColor,
//                                shape = RoundedCornerShape(15.dp)
//                            )
//                            .defaultMinSize(minWidth = 50.dp)
//                            .padding(horizontal = 15.dp, vertical = 10.dp),
//                        fontSize = 16.sp,
//                        lineHeight = 21.sp,
//                        color = Color.Black,
//                        text = "正在生成..."
//                    )
//                }
//            }
//        }
//
//        items(chatGLMViewModel.historyMessage) { message ->
//            if (message.role == ChatMessage.ChatMessageRole.ASSISTANT) {
//                ChatGLMAssistantMessage(message)
//            } else {
//                ChatGLMUserMessage(message)
//            }
//        }
//    }
//
//}
//
//@Composable
//fun ChatGLMUserMessage(message: ChatMessage) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp, bottom = 20.dp),
//        horizontalAlignment = Alignment.End
//    ) {
//        Text(
//            modifier = Modifier.padding(end = 10.dp, bottom = 3.dp),
//            fontSize = 12.sp,
//            color = Color.Black,
//            text = LocalDateTimeUtil.format(message.createTime, DatePattern.NORM_DATETIME_FORMATTER)
//        )
//        Text(
//            modifier = Modifier
//                .background(
//                    color = UserMessageColor,
//                    shape = RoundedCornerShape(15.dp)
//                )
//                .defaultMinSize(minWidth = 50.dp)
//                .padding(horizontal = 15.dp, vertical = 10.dp),
//            fontSize = 16.sp,
//            lineHeight = 21.sp,
//            color = Color.White,
//            text = message.content
//        )
//    }
//
//}
//
//@Composable
//fun ChatGLMAssistantMessage(message: ChatMessage) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(end = 20.dp, bottom = 20.dp),
//    ) {
//        Text(
//            modifier = Modifier.padding(start = 10.dp, bottom = 3.dp),
//            fontSize = 12.sp,
//            color = Color.Black,
//            text = LocalDateTimeUtil.format(message.createTime, DatePattern.NORM_DATETIME_FORMATTER)
//        )
//        Text(
//            modifier = Modifier
//                .background(
//                    color = AssistantMessageColor,
//                    shape = RoundedCornerShape(15.dp)
//                )
//                .defaultMinSize(minWidth = 50.dp)
//                .padding(horizontal = 15.dp, vertical = 10.dp),
//            fontSize = 16.sp,
//            lineHeight = 21.sp,
//            color = Color.Black,
//            text = message.content
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun GreetingChatGLMPagePreview() {
    PCMallComposeTheme {
        AiChatPage()
    }
}