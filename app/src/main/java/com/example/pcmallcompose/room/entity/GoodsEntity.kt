package com.example.pcmallcompose.room.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.model.Goods

@Entity(tableName = "goods")
data class GoodsEntity(
    @PrimaryKey var id: Int? = null,
    @ColumnInfo(name = "label") val label: String?,//标签（goods_home、goods_search、goods_category）
    @ColumnInfo(name = "search_value") val searchValue: String?,//搜索值
    @Embedded(prefix = "goods_") var goods: Goods,
)