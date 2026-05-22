package com.example.pcmallcompose.ui.page

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.pcmallcompose.ui.component.PasswordTextField
import com.example.pcmallcompose.ui.dialog.CaptchaDialog
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.ErrorMessage
import com.example.pcmallcompose.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var openCaptchaDialog by remember { mutableStateOf(false) }

    // 注册成功 → 弹窗后返回登录页
    LaunchedEffect(uiState.isUserRegistered) {
        if (uiState.isUserRegistered) {
            openCaptchaDialog = false
            MessageDialog(context, SweetAlertDialog.SUCCESS_TYPE, "注册成功！") {
                onBackClick()
            }.show()
        }
    }

    // 错误消息 → 弹窗 / Toast，然后消费
    LaunchedEffect(uiState.errorMessage) {
        when (val msg = uiState.errorMessage) {
            is ErrorMessage.Dialog -> {
                openCaptchaDialog = false
                MessageDialog(context, SweetAlertDialog.WARNING_TYPE, msg.text).show()
            }
            is ErrorMessage.Toast -> {
                Toast.makeText(context, msg.text, Toast.LENGTH_SHORT).show()
            }
            null -> {}
        }
        registerViewModel.userMessageShown()
    }

    // 验证码错误 → 刷新验证码
    LaunchedEffect(uiState.shouldRefreshCaptcha) {
        if (uiState.shouldRefreshCaptcha) {
            registerViewModel.getCaptcha()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { RegisterPageTopBar(scrollBehavior, onBackClick) },
    ) { innerPadding ->
        RegisterPageContent(
            modifier = Modifier.padding(innerPadding),
            register = { uid, password, code ->
                registerViewModel.register(uid, password, code)
            },
            captchaImage = uiState.captchaImage,
            openCaptchaDialog = openCaptchaDialog,
            onOpenValueChange = { openCaptchaDialog = it },
            getCaptcha = { registerViewModel.getCaptcha() },
            onBackClick = onBackClick
        )
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun RegisterPageContent(
    modifier: Modifier = Modifier,
    register: (String, String, String) -> Unit,
    openCaptchaDialog: Boolean = false,
    onOpenValueChange: (Boolean) -> Unit,
    captchaImage: ImageBitmap? = null,
    getCaptcha: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var uid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }
    var captchaCode by remember { mutableStateOf("") }

    var isSubmitted by remember { mutableStateOf(false) }
    var isCaptchaSubmitted by remember { mutableStateOf(false) }

    val uidError by remember { derivedStateOf { isSubmitted && uid.length != 9 } }
    val passwordLengthError by remember { derivedStateOf { isSubmitted && (password.length !in 5..16) } }
    val twoPasswordError by remember { derivedStateOf { isSubmitted && password.isNotEmpty() && password != rePassword } }
    val captchaError by remember { derivedStateOf { isCaptchaSubmitted && captchaCode.length != 5 } }

    if (!openCaptchaDialog) {
        isCaptchaSubmitted = false
        captchaCode = ""
    }

    CaptchaDialog(
        enabled = openCaptchaDialog,
        value = captchaCode,
        onValueChange = { captchaCode = it },
        onDismissRequest = {
            onOpenValueChange(false)
            isCaptchaSubmitted = false
            captchaCode = ""
        },
        onConfirmation = {
            isCaptchaSubmitted = true
            if (!captchaError) {
                register(uid, password, captchaCode)
            }
        },
        reloadCaptchaImage = { getCaptcha() },
        captchaImage = captchaImage,
        isError = captchaError,
        errorMessage = "验证码是五位字符！",
    )

    Column(modifier = modifier) {
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
                onClick = onBackClick
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    text = "返回登录",
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (uidError) {
                        Text("账号必须是9位数字！")
                    }
                },
                isError = uidError,
                singleLine = true
            )

            PasswordTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "密码") },
                supportingText = {
                    if (passwordLengthError) {
                        Text("密码必须在5~16位之间！")
                    }
                },
                isError = passwordLengthError,
                singleLine = true
            )

            PasswordTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                value = rePassword,
                onValueChange = { rePassword = it },
                label = { Text(text = "确认密码") },
                supportingText = {
                    if (twoPasswordError) {
                        Text("两次密码不一致")
                    }
                },
                isError = twoPasswordError,
                singleLine = true
            )

            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(),
                onClick = {
                    isSubmitted = true
                    if (!uidError && !passwordLengthError && !twoPasswordError) {
                        getCaptcha()
                        onOpenValueChange(true)
                    }
                }
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