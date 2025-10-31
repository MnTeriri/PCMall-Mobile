package com.example.pcmallcompose.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pcmallcompose.model.Cart
import com.example.pcmallcompose.room.converter.BigDecimalConverter
import com.example.pcmallcompose.room.converter.GoodsConverter
import com.example.pcmallcompose.room.converter.LocalDateTimeConverter
import com.example.pcmallcompose.room.dao.CartDao
import com.example.pcmallcompose.room.dao.GoodsDao
import com.example.pcmallcompose.room.dao.RemoteKeyDao
import com.example.pcmallcompose.room.entity.GoodsEntity
import com.example.pcmallcompose.room.entity.RemoteKey

@Database(entities = [GoodsEntity::class, Cart::class, RemoteKey::class], version = 1)
@TypeConverters(value = [BigDecimalConverter::class, LocalDateTimeConverter::class, GoodsConverter::class])
abstract class PCMallDatabase : RoomDatabase() {
    abstract fun goodsDao(): GoodsDao
    abstract fun cartDao(): CartDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}