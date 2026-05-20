package com.example.pcmallcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcmallcompose.core.model.Goods

@Entity(tableName = "goods")
data class GoodsEntity(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "label") val label: String,//标签（goods_home、goods_search、goods_category）
    @ColumnInfo(name = "search_value") val searchValue: String,//搜索值
    @ColumnInfo(name = "goods") val goods: Goods,
)