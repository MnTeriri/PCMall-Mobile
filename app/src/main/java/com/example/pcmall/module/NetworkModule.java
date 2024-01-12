package com.example.pcmall.module;

import com.example.pcmall.service.LoginService;
import com.example.pcmall.service.UserService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    public static String baseUrl = "https://example.com";

    @Singleton
    @Provides
    public static LoginService provideLoginService() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .build()
                .create(LoginService.class);
    }

    @Singleton
    @Provides
    public static UserService provideUserService() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .build()
                .create(UserService.class);
    }
}
