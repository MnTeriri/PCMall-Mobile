package com.example.pcmallcompose.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcmallcompose.core.model.Order
import com.example.pcmallcompose.core.model.Order.OrderState.CANCELED
import com.example.pcmallcompose.core.model.Order.OrderState.PENDING_PAYMENT
import com.example.pcmallcompose.core.model.Order.OrderState.PENDING_RECEIPT
import com.example.pcmallcompose.core.model.Order.OrderState.PENDING_SHIPMENT
import com.example.pcmallcompose.core.model.Order.OrderState.RETURNED
import com.example.pcmallcompose.core.model.Order.OrderState.RETURNING
import com.example.pcmallcompose.core.model.Order.OrderState.SUCCESS
import com.example.pcmallcompose.ui.theme.PriceColor

@Composable
fun OrderActionButtons(
    modifier: Modifier = Modifier,
    status: Order.OrderState,
    isLoading: Boolean = false,
    onPayClick: () -> Unit,
    onSuccessClick: () -> Unit,
    onCancelClick: () -> Unit,
    onRefundClick: () -> Unit,
) {
    if (status == CANCELED || status == RETURNING || status == RETURNED) {
        return
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val buttonModifier = Modifier.size(90.dp, 35.dp)
        val buttonPadding = PaddingValues(horizontal = 10.dp)

        when (status) {
            PENDING_PAYMENT -> {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    enabled = !isLoading
                ) {
                    Text("取消订单", fontSize = 13.sp)
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onPayClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PriceColor)
                ) {
                    Text("去付款", fontSize = 13.sp, color = Color.White)
                }
            }

            PENDING_SHIPMENT -> {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    enabled = !isLoading,
                ) {
                    Text("取消订单", fontSize = 13.sp)
                }
            }

            PENDING_RECEIPT -> {
                OutlinedButton(
                    onClick = onRefundClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    enabled = !isLoading,
                ) {
                    Text("申请售后", fontSize = 13.sp)
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onSuccessClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PriceColor)
                ) {
                    Text("确认签收", fontSize = 13.sp, color = Color.White)
                }
            }

            SUCCESS -> {
                OutlinedButton(
                    onClick = onRefundClick,
                    modifier = buttonModifier,
                    contentPadding = buttonPadding,
                    enabled = !isLoading,
                ) {
                    Text("申请售后", fontSize = 13.sp)
                }
            }
        }
    }
}