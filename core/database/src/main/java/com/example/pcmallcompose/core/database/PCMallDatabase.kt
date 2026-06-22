package com.example.pcmallcompose.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pcmallcompose.core.database.converter.BigDecimalConverter
import com.example.pcmallcompose.core.database.converter.GoodsConverter
import com.example.pcmallcompose.core.database.converter.GoodsListConverter
import com.example.pcmallcompose.core.database.converter.LocalDateTimeConverter
import com.example.pcmallcompose.core.database.converter.OrderAddressConverter
import com.example.pcmallcompose.core.database.converter.OrderGoodsListConverter
import com.example.pcmallcompose.core.database.dao.AddressDao
import com.example.pcmallcompose.core.database.dao.BrandDao
import com.example.pcmallcompose.core.database.dao.CartDao
import com.example.pcmallcompose.core.database.dao.CategoryDao
import com.example.pcmallcompose.core.database.dao.ChatHistoryDao
import com.example.pcmallcompose.core.database.dao.GoodsDao
import com.example.pcmallcompose.core.database.dao.OrderDao
import com.example.pcmallcompose.core.database.dao.RemoteKeyDao
import com.example.pcmallcompose.core.database.entity.AddressEntity
import com.example.pcmallcompose.core.database.entity.BrandEntity
import com.example.pcmallcompose.core.database.entity.CartEntity
import com.example.pcmallcompose.core.database.entity.CategoryEntity
import com.example.pcmallcompose.core.database.entity.ChatHistoryEntity
import com.example.pcmallcompose.core.database.entity.GoodsEntity
import com.example.pcmallcompose.core.database.entity.OrderEntity
import com.example.pcmallcompose.core.database.entity.RemoteKey

@Database(
    entities = [
        GoodsEntity::class,
        CartEntity::class,
        AddressEntity::class,
        OrderEntity::class,
        BrandEntity::class,
        CategoryEntity::class,
        ChatHistoryEntity::class,
        RemoteKey::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [
        BigDecimalConverter::class,
        LocalDateTimeConverter::class,
        GoodsConverter::class,
        GoodsListConverter::class,
        OrderAddressConverter::class,
        OrderGoodsListConverter::class
    ]
)
abstract class PCMallDatabase : RoomDatabase() {
    abstract fun goodsDao(): GoodsDao
    abstract fun cartDao(): CartDao
    abstract fun addressDao(): AddressDao
    abstract fun orderDao(): OrderDao
    abstract fun brandDao(): BrandDao
    abstract fun categoryDao(): CategoryDao
    abstract fun chatHistoryDao(): ChatHistoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}