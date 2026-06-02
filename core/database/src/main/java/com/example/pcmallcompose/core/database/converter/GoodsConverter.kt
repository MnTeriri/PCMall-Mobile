package com.example.pcmallcompose.core.database.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString
import com.example.pcmallcompose.core.model.Goods

class GoodsConverter {
    @TypeConverter
    fun fromString(value: String): Goods {
        return value.parseObject<Goods>()
    }

    @TypeConverter
    fun goodsToString(goods: Goods): String {
        return goods.toJSONString()
    }
}