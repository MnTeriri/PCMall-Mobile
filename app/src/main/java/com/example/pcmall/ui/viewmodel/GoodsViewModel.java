package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Goods;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.GoodsService;

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
    private final String TAG = "HomeViewModel";
    private GoodsService goodsService;
    @Getter
    private final MutableLiveData<ResponseResult<List<Goods>>> goodsList;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public GoodsViewModel(GoodsService goodsService) {
        this.goodsService = goodsService;
        this.goodsList = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();
        Log.d(TAG, "自动注入goodsService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void getGoodsList(Integer currentPage, Integer pageSize) {
        Disposable disposable = goodsService.searchGoodsList(currentPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseResult<List<Goods>>>() {
                    @Override
                    public void accept(ResponseResult<List<Goods>> responseResult) throws Throwable {

                        goodsList.setValue(responseResult);
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
        Log.d(TAG, "销毁GoodsViewModel，清除compositeDisposable");
    }
}