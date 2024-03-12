package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.service.OrderService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@HiltViewModel
public class MySelfViewModel extends ViewModel {
    private final String TAG = "MySelfViewModel";
    private final OrderService orderService;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public MySelfViewModel(OrderService orderService) {
        this.orderService = orderService;
        this.compositeDisposable = new CompositeDisposable();
        Log.d(TAG, "自动注入OrderService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁GoodsViewModel，清除compositeDisposable");
    }
}