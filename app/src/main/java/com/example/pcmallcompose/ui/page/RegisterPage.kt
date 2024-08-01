package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcmallcompose.R
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(onBackClick: () -> Unit = {}) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            RegisterPageTopBar(scrollBehavior, onBackClick)
        },
    ) { innerPadding ->
        RegisterPageContent(innerPadding, onBackClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPageTopBar(scrollBehavior: TopAppBarScrollBehavior, onBackClick: () -> Unit) {
    LargeTopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = "亲，欢迎注册") },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}


@Composable
fun RegisterPageContent(paddingValues: PaddingValues, onBackClick: () -> Unit) {
    Column(modifier = Modifier.padding(paddingValues)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 15.sp,
                text = "已有账号？"
            )
            TextButton(
                contentPadding = PaddingValues(start = 2.dp),
                onClick = { onBackClick() }) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 15.sp,
                    color = colorResource(R.color.yellow),
                    text = "返回登录",
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        ) {

            var text by remember { mutableStateOf("") }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = { text = it },
                label = { Text(text = "UID") }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                value = text,
                onValueChange = { text = it },
                label = { Text(text = "密码") }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                value = text,
                onValueChange = { text = it },
                label = { Text(text = "再次输入密码") }
            )

            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.yellow)
                ),
                onClick = { }
            ) { Text(text = "注册") }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPagePreview() {
    PCMallComposeTheme {
        RegisterPage()
    }
}