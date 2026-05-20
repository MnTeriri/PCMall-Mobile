package com.example.pcmallcompose.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.core.database.dao.CartDao
import com.example.pcmallcompose.core.database.dao.ChatHistoryDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): PCMallDatabase {
        return Room.databaseBuilder(context, PCMallDatabase::class.java, "pcmall").build()
    }

    @Singleton
    @Provides
    fun provideCartDao(database: PCMallDatabase): CartDao {
        return database.cartDao()
    }

    @Singleton
    @Provides
    fun provideChatHistoryDao(database: PCMallDatabase): ChatHistoryDao {
        return database.chatHistoryDao()
    }
}