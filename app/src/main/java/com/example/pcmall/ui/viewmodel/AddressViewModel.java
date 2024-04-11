package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Address;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.AddressService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class AddressViewModel extends ViewModel {
    private final String TAG = "AddressViewModel";
    public final static Integer ERROR = -2;//错误
    public final static Integer LOAD_ERROR = -1;//加载错误
    public final static Integer REFRESH_SUCCESS = 1;//刷新成功
    private final AddressService addressService;
    private final CompositeDisposable compositeDisposable;
    private final List<Address> addressList;

    @Getter
    private final MutableLiveData<List<Address>> addressLiveData;
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public AddressViewModel(AddressService addressService) {
        this.addressService = addressService;
        this.compositeDisposable = new CompositeDisposable();
        this.addressList = new ArrayList<>();
        this.addressLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        Log.d(TAG, "自动注入AddressViewModel完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void getAddressList(String uid) {
        Disposable disposable = addressService.searchAddressList(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    addressLiveData.setValue(responseResult.getData());
                    flagLiveData.setValue(REFRESH_SUCCESS);
                }, throwable -> {
                    flagLiveData.setValue(LOAD_ERROR);
                });
        compositeDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁AddressViewModel，清除compositeDisposable");
    }
}
