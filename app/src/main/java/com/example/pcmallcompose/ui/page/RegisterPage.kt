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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.pcmallcompose.ui.component.PasswordTextField
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.RegisterUiEvent
import com.example.pcmallcompose.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    onBackClick: () -> Unit = {}
) {
    val registerViewModel: RegisterViewModel = hiltViewModel()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            RegisterPageTopBar(scrollBehavior, onBackClick)
        },
    ) { innerPadding ->
        RegisterPageContent(registerViewModel, innerPadding, onBackClick)
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
    registerViewModel: RegisterViewModel,
    paddingValues: PaddingValues,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var openCaptchaDialog by remember { mutableStateOf(false) }
    var captchaImage by registerViewModel.captchaImage
    var captchaError by remember { mutableStateOf(false) }
    var captchaErrorMessage by remember { mutableStateOf("") }

    var uid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }
    val uidError by remember { derivedStateOf { uid.length != 9 } }
    val twoPasswordError by remember { derivedStateOf { password != rePassword } }


    LaunchedEffect(Unit) {
        registerViewModel.uiEvent.collect { event ->
            when (event) {
                is RegisterUiEvent.Success -> {
                    openCaptchaDialog = false
                    MessageDialog(
                        context = context,
                        alertType = SweetAlertDialog.SUCCESS_TYPE,
                        title = event.message,
                        dismissListener = { onBackClick() }
                    ).show()
                }

                is RegisterUiEvent.CaptchaError -> {
                    captchaError = true
                    captchaErrorMessage = "验证码错误！"
                }

                is RegisterUiEvent.UserExistError -> {
                    openCaptchaDialog = false
                    MessageDialog(context, SweetAlertDialog.WARNING_TYPE, "账号存在").show()
                }

                is RegisterUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(openCaptchaDialog) {
        captchaImage = null
        captchaError = false
    }

//    CaptchaDialog(
//        enabled = openCaptchaDialog,
//        onDismissRequest = {
//            openCaptchaDialog = false
//        },
//        onConfirmation = { captcha ->
//            if (!captchaError) {
//                registerViewModel.register(uid, password, captcha)
//            }
//        },
//        onReloadCaptchaImage = {
//            captchaImage = null
//            captchaError = false
//            registerViewModel.getCaptcha()
//        },
//        captchaImage = captchaImage,
//        isError = captchaError,
//        errorMessage = captchaErrorMessage,
//        validate = {
//            captchaError = it.length != 5
//            if (captchaError) {
//                captchaErrorMessage = "验证码是五位字符！"
//            }
//        }
//    )

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

            PasswordTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                value = rePassword,
                onValueChange = { rePassword = it },
                label = { Text(text = "再次输入密码") },
                supportingText = {
                    if (twoPasswordError) {
                        Text("两次密码不相同！")
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
                    if (!uidError && !twoPasswordError) {
                        registerViewModel.getCaptcha()
                        openCaptchaDialog = true
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