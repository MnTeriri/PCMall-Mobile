package com.example.pcmallcompose.application;

import android.app.Application;
import android.util.Log;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PCMallApplication extends Application {
    private final String TAG = "PCMallApplication";
//    @Inject
//    public SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "PCMallApplication启动");
//        boolean remember = sharedPreferences.getBoolean("remember", false);
//        if (!remember) {
//            Log.d(TAG, "用户没有记住账号，清空token和登录信息");
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("data", null);
//            editor.putString("token", null);
//            editor.putBoolean("remember", false);
//            editor.apply();
//        }
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("data", null);
//        editor.putString("token", null);
//        editor.apply();
    }

//    public User getUserData() {
//        User user = null;
//        String data = sharedPreferences.getString("data", null);
//        if ("".equals(data)) {
//            Log.d(TAG, "用户没登陆");
//            return null;
//        }
//        user = JSON.parseObject(data, User.class);
//        return user;
//    }
}
