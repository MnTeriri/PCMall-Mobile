package com.example.pcmall.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.pcmall.module.NetworkModule;

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
            editor.putString("data", "");
            editor.putString("token", "");
            editor.apply();
        }
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("data", "");
//        editor.putString("token", "");
//        editor.apply();
    }
}
