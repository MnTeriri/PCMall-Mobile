package com.example.pcmallcompose.ui.page

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme

@Composable
fun AddressPage(
    onBackClick: () -> Unit = {},
    onAddAddressClick: () -> Unit = {},
    onUpdateAddressClick: (Address) -> Unit = {},
) {
    Scaffold(
        topBar = { AddressPageTopBar(onBackClick) },
        bottomBar = { AddressPageBottomBar(onAddAddressClick) }
    ) { innerPadding ->

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressPageTopBar(
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text("地址管理") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun AddressPageBottomBar(
    onAddAddressClick: () -> Unit,
){
    BottomAppBar(
        containerColor = Color.White
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = onAddAddressClick
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("添加新地址")
        }
    }
}

//@Composable
//private fun AddressItemView(
//    address: Address,
//    onSelectClick: () -> Unit,
//    onEditClick: () -> Unit,
//    onDeleteClick: () -> Unit,
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            verticalAlignment = Alignment.Top
//        ) {
//            RadioButton(
//                selected = address.isDefault == 1,
//                onClick = onSelectClick
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(start = 8.dp, top = 10.dp)
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = address.receiverName ?: "",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Spacer(Modifier.width(12.dp))
//                    Text(
//                        text = address.phone ?: "",
//                        fontSize = 14.sp,
//                        color = Color.Gray
//                    )
//                    if (address.isDefault == 1) {
//                        Spacer(Modifier.weight(1f))
//                        Text(
//                            text = "默认",
//                            fontSize = 11.sp,
//                            color = MaterialTheme.colorScheme.primary,
//                        )
//                    }
//                }
//
//                Spacer(Modifier.height(4.dp))
//
//                Text(
//                    text = buildString {
//                        append(address.province ?: "")
//                        append(address.city ?: "")
//                        append(address.district ?: "")
//                        append(address.addressDetail ?: "")
//                    },
//                    fontSize = 14.sp,
//                    color = Color.DarkGray,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//
//            // 编辑按钮
//            IconButton(onClick = onEditClick) {
//                Icon(
//                    Icons.Filled.Edit,
//                    contentDescription = "编辑",
//                    modifier = Modifier.size(20.dp),
//                    tint = Color.Gray
//                )
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun AddressPagePreview() {
    PCMallComposeTheme {
        AddressPage()
    }
}