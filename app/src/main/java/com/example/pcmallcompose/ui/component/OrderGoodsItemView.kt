package com.example.pcmallcompose.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.core.model.OrderGoods
import com.example.pcmallcompose.core.network.di.NetworkModule
import com.example.pcmallcompose.ui.theme.PriceColor

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OrderGoodsItemView(goods: OrderGoods) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = "${NetworkModule.IMAGE_URL}${goods.image}",
            modifier = Modifier.size(90.dp),
            contentDescription = null,
            failure = placeholder(R.drawable.test_image)
        )

        Spacer(Modifier.width(6.dp))

        // 中间信息 — weight(1f) 吃掉图片和价格之间的所有剩余空间，不设固定宽度
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${goods.brand.bname} ${goods.gname}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = goods.description,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.width(6.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "¥${goods.price.setScale(2)}",
                fontSize = 13.sp,
                color = PriceColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "×${goods.count}",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}