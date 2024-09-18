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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pcmallcompose.R
import com.example.pcmallcompose.ui.dialog.CaptchaDialog
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    jumpToRegister: () -> Unit = {}
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LoginPageTopBar(scrollBehavior, onBackClick)
        },
    ) { innerPadding ->
        LoginPageContent(loginViewModel, innerPadding, jumpToRegister)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPageTopBar(scrollBehavior: TopAppBarScrollBehavior, onBackClick: () -> Unit) {
    LargeTopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(text = "亲，欢迎登录") },
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
fun LoginPageContent(
    loginViewModel: LoginViewModel,
    paddingValues: PaddingValues,
    jumpToRegister: () -> Unit
) {
    var openAlertDialog by remember { mutableStateOf(false) }
    var captchaImage by loginViewModel.captchaString
    var captchaError by loginViewModel.captchaError
    var captchaErrorMessage by loginViewModel.captchaErrorMessage

    var uid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(true) }

    when {
        openAlertDialog -> {
            CaptchaDialog(
                onDismissRequest = {
                    openAlertDialog = false
                    captchaImage = ""
                },
                onConfirmation = { captcha ->
                    if (!captchaError) {
                        loginViewModel.login(uid, password, captcha)
                    }
                },
                onClickCaptchaImage = {
                    captchaImage = ""
                    loginViewModel.getCaptcha()
                },
                onReloadCaptchaImage = {
                    captchaImage = ""
                    loginViewModel.getCaptcha()
                },
                captchaImage = captchaImage,
                isError = captchaError,
                errorMessage = captchaErrorMessage,
                validate = {
                    captchaError = it.length != 5
                    if (captchaError) {
                        captchaErrorMessage = "验证码是五位字符！"
                    }
                }
            )
        }
    }

    Column(modifier = Modifier.padding(paddingValues)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 15.sp,
                text = "没有账号？"
            )
            TextButton(
                contentPadding = PaddingValues(start = 2.dp),
                onClick = { jumpToRegister() }) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 15.sp,
                    color = colorResource(R.color.yellow),
                    text = "立即注册",
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        ) {


            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uid,
                onValueChange = { uid = it },
                label = { Text(text = "UID") }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "密码") }
            )

            Row(
                modifier = Modifier.padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Text(text = "记住我")

            }

            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.yellow)
                ),
                onClick = {
                    loginViewModel.getCaptcha()
                    openAlertDialog = true
                }
            ) { Text(text = "登录") }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    PCMallComposeTheme {
        LoginPage()
    }
}