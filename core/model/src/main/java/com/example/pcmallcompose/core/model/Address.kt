package com.example.pcmallcompose.core.model

import java.time.LocalDateTime

data class Address(
    var id: Int, //地址编号
    var uid: String, //用户编号
    var province: String, //省
    var city: String, //市
    var district: String, //区
    var addressDetail: String, //详细地址
    var receiverName: String, //收件人
    var phone: String, //手机号码
    var createTime: LocalDateTime, //创建时间
    var updateTime: LocalDateTime? = null, //修改时间
    var isDefault: Int, //是否选中（0不选中 1选中）
)