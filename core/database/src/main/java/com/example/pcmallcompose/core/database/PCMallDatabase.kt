package com.example.pcmallcompose.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pcmallcompose.core.database.converter.BigDecimalConverter
import com.example.pcmallcompose.core.database.converter.GoodsConverter
import com.example.pcmallcompose.core.database.converter.LocalDateTimeConverter
import com.example.pcmallcompose.core.database.dao.CartDao
import com.example.pcmallcompose.core.database.dao.GoodsDao
import com.example.pcmallcompose.core.database.dao.RemoteKeyDao
import com.example.pcmallcompose.core.database.entity.CartEntity
import com.example.pcmallcompose.core.database.entity.GoodsEntity
import com.example.pcmallcompose.core.database.entity.RemoteKey

@Database(
    entities = [GoodsEntity::class, CartEntity::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [BigDecimalConverter::class, LocalDateTimeConverter::class, GoodsConverter::class])
abstract class PCMallDatabase : RoomDatabase() {
    abstract fun goodsDao(): GoodsDao
    abstract fun cartDao(): CartDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}