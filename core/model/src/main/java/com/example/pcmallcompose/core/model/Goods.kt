package com.example.pcmallcompose.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

data class Goods(
    val id: Int, //商品编号
    val cid: Int, //分类编号，参考category的主键
    val category: Category,
    val bid: Int, //品牌编号，参考brand的主键
    val brand: Brand,
    val gname: String,//商品名称
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime,//创建时间
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime? = null, //修改时间
    val image: String, //图片
    val price: BigDecimal, //价格
    val discount: BigDecimal, //折扣
    val count: Int, //数量
    val description: String,//商品描述
    val status: GoodsState,//商品状态（0正常、1缺货、2下架）
    val isDelete: Int, //是否删除（0正常 1删除）
) {
    enum class GoodsState(
        @field:JsonValue
        val code: Int,
        val label: String
    ) {
        NORMAL(0, "正常"),
        OUT_OF_STOCK(1, "缺货"),
        OFF_SHELF(2, "下架");

        companion object {
            @JsonCreator
            fun fromCode(code: Int): GoodsState? {
                for (state in entries) {
                    if (state.code == code) {
                        return state
                    }
                }
                return null
            }
        }
    }
}