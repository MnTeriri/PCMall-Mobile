package com.example.pcmallcompose.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pcmallcompose.model.Cart
import com.example.pcmallcompose.model.RemoteKey
import com.example.pcmallcompose.room.converter.GoodsConverter
import com.example.pcmallcompose.room.converter.LocalDateTimeConverter
import com.example.pcmallcompose.room.dao.CartDao
import com.example.pcmallcompose.room.dao.RemoteKeyDao

@Database(entities = [Cart::class, RemoteKey::class], version = 1)
@TypeConverters(value = [LocalDateTimeConverter::class, GoodsConverter::class])
abstract class PCMallDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}