package com.example.pcmallcompose.core.database.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson2.parseArray
import com.alibaba.fastjson2.toJSONString
import com.example.pcmallcompose.core.model.Goods

class RecommendsConverter {
    @TypeConverter
    fun fromString(value: String): List<Goods> {
        return value.parseArray<Goods>()
    }

    @TypeConverter
    fun listToString(list: List<Goods>): String {
        return list.toJSONString()
    }
}