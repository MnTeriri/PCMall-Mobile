package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.User;
import com.example.pcmall.service.LoginService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    public LoginService loginService1;
    public LoginService loginService2;

    private final MutableLiveData<String> mText;

    @Inject
    public HomeViewModel(LoginService loginService1, LoginService loginService2) {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        Log.d("自动注入22", loginService1.toString());
        Log.d("自动注入22", loginService2.toString());
        this.loginService1 = loginService1;
        this.loginService2 = loginService2;
        User user = new User();
        Log.d("user", user);
    }

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        Log.d("自动注入22", loginService1.toString());
        Log.d("自动注入22", loginService2.toString());
    }

    public LiveData<String> getText() {
        return mText;
    }
}