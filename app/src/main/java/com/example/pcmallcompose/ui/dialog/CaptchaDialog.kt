package com.example.pcmallcompose.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CaptchaDialog(
    enabled: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmation: (captcha: String) -> Unit,
    onReloadCaptchaImage: () -> Unit,
    captchaImage: ImageBitmap? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    validate: (String) -> Unit = {},
) {
    if (!enabled) {
        return
    }

    var text by remember { mutableStateOf("") }

    AlertDialog(
        title = {
            Text(text = "请输入验证码")
        },
        text = {
            Column {
                Text(text = "请在下方输入框输入图片验证码")
                if (captchaImage == null) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(top = 5.dp)
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .size(width = 135.dp, height = 50.dp)
                            .padding(top = 5.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                onReloadCaptchaImage()
                            },
                        bitmap = captchaImage,
                        contentDescription = null,
                    )
                    TextButton(
                        modifier = Modifier
                            .size(width = Dp.Unspecified, height = 25.dp)
                            .padding(top = 5.dp)
                            .align(Alignment.CenterHorizontally),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(0),
                        onClick = {
                            onReloadCaptchaImage()
                        }
                    ) {
                        Text(
                            fontSize = 12.sp,
                            color = Color.Black,
                            text = "看不清？换一张",
                        )
                    }
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(text = "验证码") },
                    supportingText = {
                        if (isError) {
                            Text(errorMessage)
                        }
                    },
                    isError = isError,
                    singleLine = true
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    validate(text)
                    onConfirmation(text)
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("取消")
            }
        }
    )
}