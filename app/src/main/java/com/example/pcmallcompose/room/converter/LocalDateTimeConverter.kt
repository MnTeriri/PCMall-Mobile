package com.example.pcmallcompose.room.converter

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {
    @SuppressLint("NewApi")
    @TypeConverter
    fun fromString(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime): String {
        return date.toString()
    }
}