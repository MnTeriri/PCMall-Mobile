package com.example.pcmallcompose.core.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class User(
    val id: Int,
    val uid: String, //账号
    val uname: String,//用户名
    val password: String,//密码（MD5加密）
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime,//创建时间
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val loginTime: LocalDateTime,//最后一次登录时间
    val image: String,
    val isDelete: Int//是否删除（0正常 1删除）
)