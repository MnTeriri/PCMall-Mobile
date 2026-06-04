package com.example.pcmallcompose.core.database.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson2.parseArray
import com.alibaba.fastjson2.toJSONString
import com.example.pcmallcompose.core.model.OrderGoods

class OrderGoodsListConverter {
    @TypeConverter
    fun fromString(value: String): List<OrderGoods> {
        return value.parseArray<OrderGoods>()
    }

    @TypeConverter
    fun listToString(list: List<OrderGoods>): String {
        return list.toJSONString()
    }
}