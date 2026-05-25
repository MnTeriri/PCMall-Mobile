package com.example.pcmallcompose.core.network.di

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsonModule {
    @Singleton
    @Provides
    fun provideObjectMapper(): ObjectMapper = jacksonObjectMapper()
}