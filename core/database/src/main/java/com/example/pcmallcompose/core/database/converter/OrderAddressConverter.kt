package com.example.pcmallcompose.core.database.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString
import com.example.pcmallcompose.core.model.OrderAddress

class OrderAddressConverter {
    @TypeConverter
    fun fromString(value: String): OrderAddress {
        return value.parseObject<OrderAddress>()
    }

    @TypeConverter
    fun addressToString(address: OrderAddress): String {
        return address.toJSONString()
    }
}