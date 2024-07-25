package com.example.pcmallcompose.model

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

data class Address(
    var id: Int? = null, //地址编号
    var uid: String? = null, //用户编号
    var province: String? = null, //省
    var city: String? = null, //市
    var district: String? = null, //区
    var addressDetail: String? = null, //详细地址
    var receiverName: String? = null, //收件人
    var phone: String? = null, //手机号码
    var createTime: LocalDateTime? = null, //创建时间
    var updateTime: LocalDateTime? = null, //修改时间
    var isDefault: Int? = null, //是否选中（0不选中 1选中）
)

data class Brand(
    var id: Int? = null, //品牌id
    var bname: String? = null,//品牌名称
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    var createdTime: LocalDateTime? = null,//创建时间
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    var updateTime: LocalDateTime? = null,//修改时间
    var categoryCount: Long? = null,
    var image: String? = null,
    var isDelete: Int? = null, //是否删除（0正常 1删除）
){
    fun setIsDelete(isDelete: Int?) {
        this.isDelete = isDelete;
    }

    fun getIsDelete(): Int? {
        return this.isDelete;
    }
}

data class Cart(
    var id: Int? = null, //购物车信息编号
    var uid: String? = null, //用户编号
    var gid: Int? = null,//商品编号
    var goods: Goods? = null,
    var count: Int? = null, //选购数量
    var createdTime: LocalDateTime? = null, //创建时间
    var isSelect: Int? = null,//0为未选购，1为选购
)

data class Category(
    var id: Int? = null, //分类编号
    var cname: String? = null, //分类名称
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    var createdTime: LocalDateTime? = null, //创建时间
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    var updateTime: LocalDateTime? = null,//修改时间
    var isDelete: Int? = null,//是否删除（0正常 1删除）
) {
    fun setIsDelete(isDelete: Int?) {
        this.isDelete = isDelete;
    }

    fun getIsDelete(): Int? {
        return this.isDelete;
    }
}

data class Goods(
    var id: Int? = null, //商品编号
    var cid: Int? = null, //分类编号，参考category的主键
    var category: Category? = null,
    var bid: Int? = null, //品牌编号，参考brand的主键
    var brand: Brand? = null,
    var gname: String? = null,//商品名称
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    var createdTime: LocalDateTime? = null,//创建时间
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    var updateTime: LocalDateTime? = null, //修改时间
    var image: String? = null, //图片
    var price: BigDecimal? = null, //价格
    var discount: BigDecimal? = null, //折扣
    var count: Int? = null, //数量
    var description: String? = null,//商品描述
    var status: Int? = null,//商品状态（0正常、1缺货、2下架）
    var isDelete: Int? = null, //是否删除（0正常 1删除）
){
    fun setIsDelete(isDelete: Int?) {
        this.isDelete = isDelete;
    }

    fun getIsDelete(): Int? {
        return this.isDelete;
    }
}