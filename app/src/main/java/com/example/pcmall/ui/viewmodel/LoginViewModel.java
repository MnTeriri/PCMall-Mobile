package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.LoginService;
import com.example.pcmall.utils.RetrofitUtils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

@HiltViewModel
public class LoginViewModel extends ViewModel {
    private final String TAG = "LoginViewModel";
    public LoginService loginService;
    @Getter
    private final MutableLiveData<User> loginUser;
    @Getter
    private final MutableLiveData<ResponseResult<User>> loginData;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public LoginViewModel(LoginService loginService) {
        this.loginService = loginService;
        this.loginUser = new MutableLiveData<>();
        this.loginData = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();
        Log.d(TAG, "自动注入loginService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void login(String uid, String password) {
        User user = new User();
        user.setUid(uid);
        Observable<String> login = loginService.login(uid, password);
        Disposable subscribe = login.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    ResponseResult responseResult = JSON.parseObject(message, ResponseResult.class);
                    System.out.println(responseResult);
                    loginData.setValue(responseResult);
                }, throwable -> {
                    String errorMessage = RetrofitUtils.getErrorMessage(throwable);
                    ResponseResult responseResult = JSON.parseObject(errorMessage, ResponseResult.class);
                    loginData.setValue(responseResult);
                    System.out.println(responseResult);
                });
        compositeDisposable.add(subscribe);
        loginUser.setValue(user);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "销毁LoginViewModel，清除compositeDisposable");
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }
}
