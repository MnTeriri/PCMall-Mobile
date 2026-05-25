package com.example.pcmallcompose.core.database.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.core.model.Goods

class RecommendsConverter {
    @TypeConverter
    fun fromString(value: String): List<Goods> {
        return JSON.parseArray(value, Goods::class.java)
    }

    @TypeConverter
    fun listToString(list: List<Goods>): String {
        return JSON.toJSONString(list)
    }
}