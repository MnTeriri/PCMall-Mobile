package com.example.pcmallcompose.interceptor;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private final SharedPreferences sharedPreferences;

    public HeaderInterceptor(Context context) {
        sharedPreferences = context.getSharedPreferences("userData", MODE_PRIVATE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = sharedPreferences.getString("token", "");
        Request request = chain.request().newBuilder()
                .addHeader("token", token)
                .build();
        return chain.proceed(request);
    }
}
