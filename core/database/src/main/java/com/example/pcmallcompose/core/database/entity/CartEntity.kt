package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.core.model.Cart
import com.example.pcmallcompose.core.model.Goods
import java.time.LocalDateTime

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: Int, //购物车信息编号
    @ColumnInfo(name = "uid") val uid: String, //用户编号
    @ColumnInfo(name = "gid") val gid: Int,//商品编号
    @ColumnInfo(name = "goods") val goods: Goods,
    @ColumnInfo(name = "count") val count: Int, //选购数量
    @ColumnInfo(name = "create_time") val createTime: LocalDateTime, //创建时间
    @ColumnInfo(name = "is_select") val isSelect: Int,//0为未选购，1为选购
) {
    fun toCart(): Cart = Cart(
        id = id,
        uid = uid,
        gid = gid,
        goods = goods,
        count = count,
        createTime = createTime,
        isSelect = isSelect,
    )

    companion object {
        @JvmStatic
        fun fromCart(cart: Cart): CartEntity = CartEntity(
            id = cart.id,
            uid = cart.uid,
            gid = cart.gid,
            goods = cart.goods,
            count = cart.count,
            createTime = cart.createTime,
            isSelect = cart.isSelect,
        )
    }
}