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
    private final MutableLiveData<ResponseResult<User>> loginData;
    @Getter
    private final MutableLiveData<String> captchaImageString;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public LoginViewModel(LoginService loginService) {
        this.loginService = loginService;
        this.loginData = new MutableLiveData<>();
        this.captchaImageString = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();
        Log.d(TAG, "自动注入loginService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void login(String uid, String password, String code) {
        Observable<String> observable = loginService.login(uid, password, code);
        Disposable subscribe = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    ResponseResult responseResult = JSON.parseObject(message, ResponseResult.class);
                    Log.d(TAG, "登录信息：" + responseResult.toString());
                    loginData.setValue(responseResult);
                }, throwable -> {
                    String errorMessage = RetrofitUtils.getErrorMessage(throwable);
                    ResponseResult responseResult = JSON.parseObject(errorMessage, ResponseResult.class);
                    Log.d(TAG, "登录信息：" + responseResult.toString());
                    loginData.setValue(responseResult);
                });
        compositeDisposable.add(subscribe);
    }

    public void captchaImageString() {
        Observable<String> observable = loginService.getCaptcha();
        Disposable disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageString -> {
                    captchaImageString.setValue(imageString);
                    Log.d(TAG, "验证码图片：" + imageString);
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
