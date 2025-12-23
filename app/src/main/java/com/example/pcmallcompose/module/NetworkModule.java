package com.example.pcmallcompose.module;

import android.content.Context;

import com.example.pcmallcompose.interceptor.HeaderInterceptor;
import com.example.pcmallcompose.service.CartService;
import com.example.pcmallcompose.service.GoodsService;
import com.example.pcmallcompose.service.ImageService;
import com.example.pcmallcompose.service.LoginRegisterService;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    private static final String TAG = "NetworkModule";
    private static final String BASE_URL = "http://172.20.10.2:13000/api/";
    public static final String IMAGE_URL = BASE_URL + "image/";
    private static OkHttpClient okHttpClientInstance;

    public static OkHttpClient getOkHttpClientInstance(Context context) {
        if (okHttpClientInstance == null) {
            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            okHttpClientInstance = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .addInterceptor(new HeaderInterceptor(context))
                    .build();
        }
        return okHttpClientInstance;
    }

    @Singleton
    @Provides
    public static LoginRegisterService provideLoginService(@ApplicationContext Context context) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(LoginRegisterService.class);
    }

    @Singleton
    @Provides
    public static CartService provideCartService(@ApplicationContext Context context) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL + "mobile/cart/")
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(CartService.class);
    }

    @Singleton
    @Provides
    public static GoodsService provideGoodsService(@ApplicationContext Context context) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL + "mobile/goods/")
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(GoodsService.class);
    }

    @Singleton
    @Provides
    public static ImageService provideImageService(@ApplicationContext Context context) {
        return new Retrofit.Builder()
                .baseUrl(IMAGE_URL)
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(ImageService.class);
    }
}