package com.example.pcmallcompose.ui.page

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.pcmallcompose.application.LocalUserData
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.model.dto.AddressDTO
import com.example.pcmallcompose.ui.ErrorMessage
import com.example.pcmallcompose.ui.dialog.MessageDialog
import com.example.pcmallcompose.ui.theme.PCMallComposeTheme
import com.example.pcmallcompose.viewmodel.AddressEditViewModel
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressEditPage(
    isNewAddress: Boolean = true,
    address: Address? = null,
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val user = LocalUserData.current!!
    val viewModel: AddressEditViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var province by remember { mutableStateOf(address?.province ?: "") }
    var city by remember { mutableStateOf(address?.city ?: "") }
    var district by remember { mutableStateOf(address?.district ?: "") }
    var addressDetail by remember { mutableStateOf(address?.addressDetail ?: "") }
    var receiverName by remember { mutableStateOf(address?.receiverName ?: "") }
    var phone by remember { mutableStateOf(address?.phone ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault == 1) }

    LaunchedEffect(uiState.isEditSuccess) {
        if (uiState.isEditSuccess) {
            MessageDialog(context, SweetAlertDialog.SUCCESS_TYPE, "操作成功！") {
                onBackClick()
            }.show()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        when (val msg = uiState.errorMessage) {
            is ErrorMessage.Dialog -> {
                MessageDialog(context, SweetAlertDialog.WARNING_TYPE, msg.text).show()
            }

            is ErrorMessage.Toast -> {
                Toast.makeText(context, msg.text, Toast.LENGTH_SHORT).show()
            }

            null -> {}
        }
        viewModel.errorMessageShown()
    }

    Scaffold(
        topBar = {
            AddressEditPageTopBar(
                isNewAddress = isNewAddress,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AddressEditPageBottomBar(
                onEditAddressClick = {
                    if (isNewAddress) {
                        val data = AddressDTO(
                            uid = user.uid,
                            province = province,
                            city = city,
                            district = district,
                            addressDetail = addressDetail,
                            receiverName = receiverName,
                            phone = phone,
                            isDefault = if (isDefault) 1 else 0
                        )
                        viewModel.addAddress(data)
                    } else {
                        val data = AddressDTO(
                            id = address?.id,
                            uid = user.uid,
                            province = province,
                            city = city,
                            district = district,
                            addressDetail = addressDetail,
                            receiverName = receiverName,
                            phone = phone,
                            isDefault = if (isDefault) 1 else 0
                        )
                        viewModel.updateAddress(data)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 地址信息卡片
            AddressFormCard(
                province = province,
                city = city,
                district = district,
                receiverName = receiverName,
                phone = phone,
                addressDetail = addressDetail,
                onProvinceChange = { province = it },
                onCityChange = { city = it },
                onDistrictChange = { district = it },
                onReceiverNameChange = { receiverName = it },
                onPhoneChange = { phone = it },
                onAddressDetailChange = { addressDetail = it },
            )
            // 默认地址卡片
            DefaultAddressCard(
                isDefault = isDefault,
                onCheckedChange = { isDefault = it },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressEditPageTopBar(
    isNewAddress: Boolean,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(if (isNewAddress) "添加地址" else "编辑地址") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun AddressEditPageBottomBar(
    onEditAddressClick: () -> Unit
) {
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
            onClick = onEditAddressClick
        ) {
            Text("保存")
        }
    }
}

@Composable
private fun AddressFormCard(
    province: String,
    city: String,
    district: String,
    receiverName: String,
    phone: String,
    addressDetail: String,
    onProvinceChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onReceiverNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressDetailChange: (String) -> Unit,
) {
    val context = LocalContext.current
    val addressPicker = remember {
        AddressPicker(context as Activity).apply {
            setAddressMode(AddressMode.PROVINCE_CITY_COUNTY)
        }
    }

    LaunchedEffect(province, city, district) {
        addressPicker.setDefaultValue(province,city,district)
    }

    LaunchedEffect(Unit) {
        addressPicker.setOnAddressPickedListener { province, city, district ->
            onProvinceChange(province.name)
            onCityChange(city.name)
            onDistrictChange(district.name)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            FormRow(label = "收货姓名") {
                OutlinedTextField(
                    value = receiverName,
                    onValueChange = onReceiverNameChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("收货人姓名") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    )
                )
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            FormRow(label = "手机号码") {
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("收货人手机号码") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    )
                )
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            FormRow(label = "收货地区") {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { addressPicker.show() },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = if ("$province$city$district".isEmpty()) "省市区县、乡镇" else "$province $city $district",
                            color = TextFieldDefaults.colors().focusedPlaceholderColor
                        )
                    }
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

            FormRow(label = "详细地址") {
                OutlinedTextField(
                    value = addressDetail,
                    onValueChange = onAddressDetailChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("街道、楼牌号") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    )
                )
            }
        }
    }
}

@Composable
private fun FormRow(
    label: String,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            modifier = Modifier.width(75.dp)
        )
        Spacer(Modifier.width(8.dp))
        content()
    }
}

@Composable
private fun DefaultAddressCard(
    isDefault: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("设为默认地址", fontSize = 15.sp)
            Checkbox(checked = isDefault, onCheckedChange = onCheckedChange)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddressAddPagePreview() {
    PCMallComposeTheme {
        AddressEditPage()
    }
}