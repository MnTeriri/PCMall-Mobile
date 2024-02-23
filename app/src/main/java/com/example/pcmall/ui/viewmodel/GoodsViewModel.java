package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Goods;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.GoodsService;

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
public class GoodsViewModel extends ViewModel {
    private final String TAG = "GoodsViewModel";
    private GoodsService goodsService;
    private List<Goods> goodsList;
    @Getter
    private final MutableLiveData<List<Goods>> goodsLiveData;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public GoodsViewModel(GoodsService goodsService) {
        this.goodsService = goodsService;
        this.goodsLiveData = new MutableLiveData<>();
        this.goodsList = new ArrayList<>();
        this.compositeDisposable = new CompositeDisposable();
        getGoodsList(1, 20, true);
        Log.d(TAG, "自动注入goodsService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void getGoodsList(Integer currentPage, Integer pageSize, boolean reFresh) {
        Disposable disposable = goodsService.searchGoodsList(currentPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    if (reFresh) {
                        goodsList.clear();
                        goodsList = responseResult.getData();
                    } else {
                        goodsList.addAll(responseResult.getData());
                    }
                    goodsLiveData.setValue(goodsList);
                }, throwable -> {
                    goodsLiveData.setValue(null);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁GoodsViewModel，清除compositeDisposable");
    }
}