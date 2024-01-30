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

@HiltViewModel
public class LoginRegisterViewModel extends ViewModel {
    private final String TAG = "LoginViewModel";
    public LoginService loginService;
    @Getter
    private final MutableLiveData<ResponseResult<User>> loginResponse;
    @Getter
    private final MutableLiveData<ResponseResult<String>> registerResponse;
    @Getter
    private final MutableLiveData<ResponseResult<String>> captchaImageResponse;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public LoginRegisterViewModel(LoginService loginService) {
        this.loginService = loginService;
        this.loginResponse = new MutableLiveData<>();
        this.registerResponse = new MutableLiveData<>();
        this.captchaImageResponse = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();
        Log.d(TAG, "自动注入loginService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void login(String uid, String password, String code) {
        Disposable disposable = loginService.login(uid, password, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, "登录信息：" + responseResult);
                    loginResponse.setValue(responseResult);
                }, throwable -> {
                    ResponseResult<User> responseResult = RetrofitUtils.getErrorMessage(throwable);
                    Log.d(TAG, "登录信息：" + responseResult);
                    loginResponse.setValue(responseResult);
                });
        compositeDisposable.add(disposable);
    }

    public void register(String uid, String password, String code) {
        Disposable disposable = loginService.register(uid, password, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseResult<String>>() {
                    @Override
                    public void accept(ResponseResult<String> responseResult) throws Throwable {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {

                    }
                });
        compositeDisposable.add(disposable);
    }


    public void captchaImageString() {
        Disposable disposable = loginService.getCaptcha()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseResult<String>>() {
                    @Override
                    public void accept(ResponseResult<String> responseResult) throws Throwable {
                        captchaImageResponse.setValue(responseResult);
                        Log.d(TAG, "验证码图片：" + responseResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {

                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁LoginViewModel，清除compositeDisposable");
    }
}
