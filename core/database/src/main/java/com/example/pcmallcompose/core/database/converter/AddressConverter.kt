package com.example.pcmallcompose.core.database.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson2.parseObject
import com.alibaba.fastjson2.toJSONString
import com.example.pcmallcompose.core.model.Address

class AddressConverter {
    @TypeConverter
    fun fromString(value: String): Address {
        return value.parseObject<Address>()
    }

    @TypeConverter
    fun addressToString(address: Address): String {
        return address.toJSONString()
    }
}