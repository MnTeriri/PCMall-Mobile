package com.example.pcmall.module;

import android.content.Context;

import com.example.pcmall.service.LoginService;
import com.example.pcmall.service.UserService;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    public static String baseUrl = "http://192.168.31.109:10000/api/";
    private static OkHttpClient okHttpClientInstance;

    public static OkHttpClient setOkHttpClientInstance(Context context) {
        if (okHttpClientInstance == null) {
            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            okHttpClientInstance = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();
        }
        return okHttpClientInstance;
    }

    @Singleton
    @Provides
    public static LoginService provideLoginService() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClientInstance)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(LoginService.class);
    }

    @Singleton
    @Provides
    public static UserService provideUserService() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(UserService.class);
    }
}
