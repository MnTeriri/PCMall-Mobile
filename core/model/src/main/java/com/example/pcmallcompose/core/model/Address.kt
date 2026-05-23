package com.example.pcmallcompose.core.model

import java.time.LocalDateTime

data class Address(
    val id: Int, //地址编号
    val uid: String, //用户编号
    val province: String, //省
    val city: String, //市
    val district: String, //区
    val addressDetail: String, //详细地址
    val receiverName: String, //收件人
    val phone: String, //手机号码
    val createTime: LocalDateTime, //创建时间
    val updateTime: LocalDateTime? = null, //修改时间
    val isDefault: Int, //是否选中（0不选中 1选中）
)