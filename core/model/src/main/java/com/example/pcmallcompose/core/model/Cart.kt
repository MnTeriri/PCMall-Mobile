package com.example.pcmallcompose.core.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class Cart(
    val id: Int, //购物车信息编号
    val uid: String, //用户编号
    val gid: Int,//商品编号
    val goods: Goods? = null,
    val count: Int, //选购数量
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val createdTime: LocalDateTime, //创建时间
    val isSelect: Int,//0为未选购，1为选购
)