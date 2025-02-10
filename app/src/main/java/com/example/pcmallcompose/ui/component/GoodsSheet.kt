package com.example.pcmallcompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pcmallcompose.R
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.module.NetworkModule
import com.example.pcmallcompose.ui.theme.BackgroundColor
import com.example.pcmallcompose.ui.theme.PriceColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun GoodsSheet(
    enabled: Boolean = false,
    goods: Goods,
    onDismissRequest: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!enabled) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {}
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .background(BackgroundColor)
        ) {
            val (content, button) = createRefs()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 15.dp)
            ) {
                item {
                    GlideImage(
                        model = NetworkModule.IMAGE_URL + goods.image,
                        modifier = Modifier.fillMaxWidth(),
                        contentDescription = null,
                        failure = placeholder(R.drawable.test_image),
                        contentScale = ContentScale.FillWidth
                    )
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "${goods.brand?.bname} ${goods.gname}",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )

                            Text(
                                modifier = Modifier.padding(top = 3.dp),
                                text = "${goods.description}",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )

                            Text(
                                modifier = Modifier.padding(top = 3.dp),
                                text = "￥${goods.price?.setScale(2)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PriceColor
                            )
                        }

                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "说明书",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                            )

//                            medicineData.forEachIndexed { index, data ->
//                                if (index != 0) {
//                                    HorizontalDivider(thickness = 0.7.dp, color = BackgroundColor)
//                                }
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 15.dp)
//                                ) {
//                                    Text(
//                                        modifier = Modifier
//                                            .fillMaxWidth(0.3f),
//                                        text = "${data["name"]}",
//                                        fontSize = 15.sp,
//                                        color = Color.Gray
//                                    )
//
//                                    Text(
//                                        modifier = Modifier.fillMaxWidth(),
//                                        text = "${if (data["value"] == null || data["value"] == "") "---" else data["value"]}",
//                                        fontSize = 15.sp,
//                                        color = Color.Black
//                                    )
//                                }
//                            }

                        }

                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.08f)
                    .fillMaxWidth()
                    .background(Color.White)
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom)
                    },
                verticalArrangement = Arrangement.Bottom
            ) {
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp, start = 25.dp, end = 25.dp),
                    colors = ButtonDefaults.buttonColors(),
                    onClick = {

                    }
                ) {
                    Text(text = "加入购物车")
                }
            }
        }
    }
}