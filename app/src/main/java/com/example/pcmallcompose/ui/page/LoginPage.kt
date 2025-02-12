package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.pcmallcompose.model.response.ResponseCode
import com.example.pcmallcompose.ui.component.PasswordTextField
import com.example.pcmallcompose.ui.dialog.CaptchaDialog
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.LoginViewModel
import com.example.pcmallcompose.viewmodel.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    onBackClick: () -> Unit = {},
    jumpToRegister: () -> Unit = {}
) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LoginPageTopBar(scrollBehavior, onBackClick)
        },
    ) { innerPadding ->
        LoginPageContent(loginViewModel, innerPadding, onBackClick, jumpToRegister)
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
    onBackClick: () -> Unit,
    jumpToRegister: () -> Unit
) {
    val context = LocalContext.current

    var openCaptchaDialog by remember { mutableStateOf(false) }
    var captchaImage by loginViewModel.captchaImage
    var captchaError by remember { mutableStateOf(false) }
    var captchaErrorMessage by remember { mutableStateOf("") }

    var uid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRemember by remember { mutableStateOf(true) }

    val uidError by remember { derivedStateOf { uid.length != 9 } }

    LaunchedEffect(Unit) {
        loginViewModel.uiState.collect { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    openCaptchaDialog = false
                    MessageDialog(
                        context = context,
                        alertType = SweetAlertDialog.SUCCESS_TYPE,
                        title = uiState.message,
                        dismissListener = { onBackClick() }
                    ).show()
                }

                is UiState.Error -> {
                    when (uiState.code) {
                        ResponseCode.CAPTCHA_ERROR.code -> {
                            captchaError = true
                            captchaErrorMessage = "验证码错误！"
                        }

                        ResponseCode.ACCOUNT_ERROR.code -> {
                            openCaptchaDialog = false
                            MessageDialog(context, SweetAlertDialog.WARNING_TYPE, uiState.message).show()
                        }

                        null -> {
                            Toast.makeText(context, "错误！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is UiState.Loading -> {}
                is UiState.Idle -> {}
            }
        }
    }

    LaunchedEffect(openCaptchaDialog) {
        captchaImage = null
        captchaError = false
    }

    CaptchaDialog(
        enabled = openCaptchaDialog,
        onDismissRequest = {
            openCaptchaDialog = false
        },
        onConfirmation = { captcha ->
            if (!captchaError) {
                loginViewModel.login(uid, password, captcha, isRemember)
            }
        },
        onReloadCaptchaImage = {
            captchaImage = null
            captchaError = false
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
                    color = MaterialTheme.colorScheme.primary,
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
                label = { Text(text = "账号") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                supportingText = {
                    if (uid.isNotEmpty() && uidError) {
                        Text("账号必须是9位数字！")
                    }
                },
                isError = if (uid.isEmpty()) false else uidError,
                singleLine = true
            )

            PasswordTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "密码") },
                singleLine = true
            )

            Row(
                modifier = Modifier.padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = isRemember,
                    onCheckedChange = { isRemember = it }
                )
                Text(text = "记住我")
            }

            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                colors = ButtonDefaults.buttonColors(),
                onClick = {
                    if (!uidError) {
                        loginViewModel.getCaptcha()
                        openCaptchaDialog = true
                    }
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