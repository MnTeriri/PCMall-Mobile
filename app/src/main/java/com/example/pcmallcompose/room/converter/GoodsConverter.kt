package com.example.pcmallcompose.room.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.Goods

class GoodsConverter {
    @TypeConverter
    fun fromString(value: String?): Goods? {
        if (value == null) {
            return null
        }
        return JSON.parseObject(value, Goods::class.java)
    }

    @TypeConverter
    fun goodsToString(goods: Goods?): String? {
        if (goods == null) {
            return null
        }
        return JSON.toJSONString(goods)
    }
}