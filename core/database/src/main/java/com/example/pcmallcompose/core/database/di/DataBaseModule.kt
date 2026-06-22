package com.example.pcmallcompose.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.dao.AddressDao
import com.example.pcmallcompose.core.database.dao.BrandDao
import com.example.pcmallcompose.core.database.dao.CartDao
import com.example.pcmallcompose.core.database.dao.CategoryDao
import com.example.pcmallcompose.core.database.dao.ChatHistoryDao
import com.example.pcmallcompose.core.database.dao.GoodsDao
import com.example.pcmallcompose.core.database.dao.OrderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): PCMallDatabase = Room.databaseBuilder(context, PCMallDatabase::class.java, "pcmall").build()

    @Singleton
    @Provides
    fun provideGoodsDao(database: PCMallDatabase): GoodsDao = database.goodsDao()

    @Singleton
    @Provides
    fun provideCartDao(database: PCMallDatabase): CartDao = database.cartDao()

    @Singleton
    @Provides
    fun provideAddressDao(database: PCMallDatabase): AddressDao = database.addressDao()

    @Singleton
    @Provides
    fun provideOrderDao(database: PCMallDatabase): OrderDao = database.orderDao()

    @Singleton
    @Provides
    fun provideBrandDao(database: PCMallDatabase): BrandDao = database.brandDao()

    @Singleton
    @Provides
    fun provideCategoryDao(database: PCMallDatabase): CategoryDao = database.categoryDao()

    @Singleton
    @Provides
    fun provideChatHistoryDao(database: PCMallDatabase): ChatHistoryDao = database.chatHistoryDao()
}