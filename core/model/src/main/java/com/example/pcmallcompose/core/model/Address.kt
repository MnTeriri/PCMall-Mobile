package com.example.pcmallcompose.core.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
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
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime, //创建时间
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime? = null, //修改时间
    val isDefault: Int, //是否选中（0不选中 1选中）
)