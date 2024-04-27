package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.service.OrderService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class MySelfViewModel extends ViewModel {
    private final String TAG = "MySelfViewModel";
    private final OrderService orderService;
    private final CompositeDisposable compositeDisposable;

    @Getter
    private final MutableLiveData<Long> notPayCountLiveData;//未付款订单个数
    @Getter
    private final MutableLiveData<Long> notSendCountLiveData; //待发货订单个数
    @Getter
    private final MutableLiveData<Long> notDeliverCountLiveData;//待收货订单个数
    @Getter
    private final MutableLiveData<Long> finishCountLiveData;//已完成订单个数
    @Getter
    private final MutableLiveData<Long> refundCountLiveData;//退款售后订单个数
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public MySelfViewModel(OrderService orderService) {
        this.orderService = orderService;
        this.compositeDisposable = new CompositeDisposable();
        this.notPayCountLiveData = new MutableLiveData<>();
        this.notSendCountLiveData = new MutableLiveData<>();
        this.notDeliverCountLiveData = new MutableLiveData<>();
        this.finishCountLiveData = new MutableLiveData<>();
        this.refundCountLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        Log.d(TAG, "自动注入OrderService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    //未付款订单个数
    public void getNotPayCount(String uid) {
        Disposable disposable = orderService.getRecordsFiltered("", uid, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> notPayCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    //待发货订单个数
    public void getNotSendCount(String uid) {
        Disposable disposable = orderService.getRecordsFiltered("", uid, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> notSendCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    //待收货订单个数
    public void getNotDeliverCount(String uid) {
        Disposable disposable = orderService.getRecordsFiltered("", uid, 2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> notDeliverCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    //已完成订单个数
    public void getFinishCount(String uid) {
        Disposable disposable = orderService.getRecordsFiltered("", uid, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> finishCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    //退款售后订单个数
    public void getRefundCount(String uid) {
        Disposable disposable = orderService.getRecordsFiltered("", uid, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> refundCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁MySelfViewModel，清除compositeDisposable");
    }
}