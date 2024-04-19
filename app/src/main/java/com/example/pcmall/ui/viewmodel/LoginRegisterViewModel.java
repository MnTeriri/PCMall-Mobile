package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.LoginRegisterService;
import com.example.pcmall.utils.RetrofitUtils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class LoginRegisterViewModel extends ViewModel {
    private final String TAG = "LoginRegisterViewModel";
    public LoginRegisterService loginRegisterService;
    private final CompositeDisposable compositeDisposable;

    @Getter
    private final MutableLiveData<ResponseResult<User>> loginResponse;//登录信息
    @Getter
    private final MutableLiveData<String> registerResponse;//注册信息
    @Getter
    private final MutableLiveData<String> captchaResponse;//验证码
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public LoginRegisterViewModel(LoginRegisterService loginRegisterService) {
        this.loginRegisterService = loginRegisterService;
        this.compositeDisposable = new CompositeDisposable();
        this.loginResponse = new MutableLiveData<>();
        this.registerResponse = new MutableLiveData<>();
        this.captchaResponse = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        Log.d(TAG, "自动注入loginService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void login(String uid, String password, String code) {
        Disposable disposable = loginRegisterService.login(uid, password, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, "登录信息：" + responseResult);
                    loginResponse.setValue(responseResult);
                }, throwable -> {
                    ResponseResult<String> message = RetrofitUtils.getErrorMessage(throwable);
                    if (message != null) {
                        flagLiveData.setValue(message.getCode());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void register(String uid, String password, String code) {
        Disposable disposable = loginRegisterService.register(uid, password, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, "注册信息：" + responseResult);
                    registerResponse.setValue("注册成功");
                }, throwable -> {
                    ResponseResult<String> message = RetrofitUtils.getErrorMessage(throwable);
                    if (message != null) {
                        flagLiveData.setValue(message.getCode());
                    }
                });
        compositeDisposable.add(disposable);
    }


    public void getCaptcha() {
        Disposable disposable = loginRegisterService.getCaptcha()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    captchaResponse.setValue(responseResult.getData());
                }, throwable -> {
                    flagLiveData.setValue(ResponseCode.ERROR.getCode());
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
