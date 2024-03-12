package com.example.pcmall.module;

import android.content.Context;
import android.util.Log;

import com.example.pcmall.converter.FastJsonConverterFactory;
import com.example.pcmall.interceptor.HeaderInterceptor;
import com.example.pcmall.service.CartService;
import com.example.pcmall.service.CategoryService;
import com.example.pcmall.service.GoodsService;
import com.example.pcmall.service.LoginRegisterService;
import com.example.pcmall.service.OrderService;
import com.example.pcmall.service.UserService;
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

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    private static final String TAG = "NetworkModule";
    public static String baseUrl = "http://192.168.31.109:10000/api/";
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
        Log.d(TAG, "LoginRegisterServices生成");
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(LoginRegisterService.class);
    }

    @Singleton
    @Provides
    public static GoodsService provideGoodsService(@ApplicationContext Context context) {
        Log.d(TAG, "GoodsService生成");
        return new Retrofit.Builder()
                .baseUrl(baseUrl + "mobile/goods/")
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(GoodsService.class);
    }

    @Singleton
    @Provides
    public static CategoryService provideCategoryService(@ApplicationContext Context context) {
        Log.d(TAG, "CategoryService生成");
        return new Retrofit.Builder()
                .baseUrl(baseUrl + "mobile/category/")
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(CategoryService.class);
    }

    @Singleton
    @Provides
    public static CartService provideCartService(@ApplicationContext Context context) {
        Log.d(TAG, "CartService生成");
        return new Retrofit.Builder()
                .baseUrl(baseUrl + "mobile/cart/")
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(CartService.class);
    }

    @Singleton
    @Provides
    public static OrderService provideOrderService(@ApplicationContext Context context) {
        Log.d(TAG, "OrderService生成");
        return new Retrofit.Builder()
                .baseUrl(baseUrl + "mobile/order/")
                .client(getOkHttpClientInstance(context))
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(OrderService.class);
    }

    @Singleton
    @Provides
    public static UserService provideUserService() {
        Log.d(TAG, "UserService生成");
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(UserService.class);
    }
}
