package com.example.pcmallcompose.core.network.di

import android.content.Context
import com.example.pcmallcompose.core.network.interceptor.HeaderInterceptor
import com.example.pcmallcompose.core.network.service.CartService
import com.example.pcmallcompose.core.network.service.GoodsService
import com.example.pcmallcompose.core.network.service.ImageService
import com.example.pcmallcompose.core.network.service.LoginRegisterService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.sse.EventSource
import okhttp3.sse.EventSources
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TAG = "NetworkModule"
    const val BASE_URL: String = "http://172.20.10.2:10000/api/"
    const val IMAGE_URL = BASE_URL + "image/"

    @Singleton
    @Provides
    fun provideOkHttpClientInstance(@ApplicationContext context: Context): OkHttpClient {
        val cookieJar: ClearableCookieJar =
            PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(HeaderInterceptor(context))
            .build()
    }

    @Singleton
    @Provides
    fun provideEventSourceFactory(): EventSource.Factory {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS) //永不超时
            .build()
        return EventSources.createFactory(okHttpClient)
    }

    @Singleton
    @Provides
    fun provideLoginService(okHttpClient: OkHttpClient): LoginRegisterService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(LoginRegisterService::class.java)
    }

    @Singleton
    @Provides
    fun provideCartService(okHttpClient: OkHttpClient): CartService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL + "mobile/cart/")
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(CartService::class.java)
    }

    @Singleton
    @Provides
    fun provideGoodsService(okHttpClient: OkHttpClient): GoodsService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL + "mobile/goods/")
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(GoodsService::class.java)
    }

    @Singleton
    @Provides
    fun provideImageService(okHttpClient: OkHttpClient): ImageService {
        return Retrofit.Builder()
            .baseUrl(IMAGE_URL)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(ImageService::class.java)
    }
}