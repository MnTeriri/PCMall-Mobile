package com.example.pcmall.application;

import android.app.Application;

import com.example.pcmall.module.NetworkModule;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PCMallApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkModule.setOkHttpClientInstance(getApplicationContext());
    }
}
