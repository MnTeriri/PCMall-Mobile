package com.example.pcmall.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.model.User;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PCMallApplication extends Application {
    private final String TAG = "PCMallApplication";
    @Inject
    public SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        //NetworkModule.getOkHttpClientInstance(getApplicationContext());
        Log.d(TAG, "PCMallApplication启动");
        boolean remember = sharedPreferences.getBoolean("remember", false);
        if (!remember) {
            Log.d(TAG, "用户没有记住账号，清空token和登录信息");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("data", null);
            editor.putString("token", null);
            editor.apply();
        }
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("data", null);
//        editor.putString("token", null);
//        editor.apply();
    }

    public User getUserData() {
        User user = null;
        String data = sharedPreferences.getString("data", null);
        if ("".equals(data)) {
            Log.d(TAG, "用户没登陆");
            return null;
        }
        user = JSON.parseObject(data, User.class);
        return user;
    }
}
