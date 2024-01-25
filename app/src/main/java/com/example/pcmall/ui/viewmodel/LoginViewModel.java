package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.LoginService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class LoginViewModel extends ViewModel {
    public LoginService loginService;
    @Getter
    private final MutableLiveData<User> loginUser;
    @Getter
    private final MutableLiveData<ResponseResult<User>> loginData;

    @Inject
    public LoginViewModel(LoginService loginService) {
        this.loginService = loginService;
        this.loginUser = new MutableLiveData<>();
        this.loginData = new MutableLiveData<>();
        Log.d("LoginViewModel", "自动注入loginService完成");
        Log.d("LoginViewModel", "MutableLiveData初始化完成");
    }

    public void login(String uid, String password) {
        User user = new User();
        user.setUid(uid);
        Observable<String> login = loginService.login(uid, password);
        Disposable subscribe = login.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Throwable {
                        System.out.println(s);
                    }
                });

        loginUser.setValue(user);
    }

}
