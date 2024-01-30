package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.User;
import com.example.pcmall.service.LoginRegisterService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    @Inject
    public HomeViewModel(LoginRegisterService loginRegisterService1, LoginRegisterService loginRegisterService2) {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

    }

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}