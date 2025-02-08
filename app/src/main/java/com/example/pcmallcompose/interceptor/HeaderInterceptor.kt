package com.example.pcmallcompose.interceptor

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(context: Context) : Interceptor {
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("userData", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferences.getString("token", null)
        var request = chain.request()
        if (token != null) {
            request = request.newBuilder()
                .addHeader("token", token)
                .build()
        }
        return chain.proceed(request)
    }
}