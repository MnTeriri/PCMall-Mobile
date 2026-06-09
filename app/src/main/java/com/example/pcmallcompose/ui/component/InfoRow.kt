package com.example.pcmallcompose.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    fontSize: TextUnit = 16.sp,
    labelColor: Color = Color.Black,
    valueColor: Color = Color.Black,
) {
    Row(
        modifier = modifier
            .height(45.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = fontSize, color = labelColor)
        Text(text = value, fontSize = fontSize, color = valueColor)
    }
}