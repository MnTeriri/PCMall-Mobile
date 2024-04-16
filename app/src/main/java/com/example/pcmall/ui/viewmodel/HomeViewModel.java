package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Goods;
import com.example.pcmall.service.GoodsService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final String TAG = "HomeViewModel";
    public final static Integer ERROR = -2;//错误
    public final static Integer LOAD_ERROR = -1;
    public final static Integer LOAD_MORE_SUCCESS = 1;
    public final static Integer REFRESH_SUCCESS = 2;
    private final GoodsService goodsService;
    private final CompositeDisposable compositeDisposable;
    private List<Goods> goodsList;
    private List<Goods> searchList;

    @Getter
    private final MutableLiveData<List<Goods>> goodsLiveData;//主页商品信息
    @Getter
    private final MutableLiveData<List<Goods>> searchLiveData;//搜索页商品信息
    @Getter
    private final MutableLiveData<Long> totalCountLiveData;//主页商品信息总数量
    @Getter
    private final MutableLiveData<Long> searchCountLiveData;//搜索页商品信息总数量
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public HomeViewModel(GoodsService goodsService) {
        this.goodsService = goodsService;
        this.compositeDisposable = new CompositeDisposable();
        this.goodsLiveData = new MutableLiveData<>();
        this.searchLiveData = new MutableLiveData<>();
        this.totalCountLiveData = new MutableLiveData<>();
        this.searchCountLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        this.goodsList = new ArrayList<>();
        this.searchList = new ArrayList<>();
        Log.d(TAG, "自动注入goodsService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void getGoodsList(Integer currentPage, Integer pageSize, boolean reFresh) {
        Disposable disposable = goodsService.searchGoodsList("", currentPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    if (reFresh) {
                        goodsList.clear();
                        goodsList = responseResult.getData();
                        flagLiveData.setValue(HomeViewModel.REFRESH_SUCCESS);
                    } else {
                        goodsList.addAll(responseResult.getData());
                        flagLiveData.setValue(HomeViewModel.LOAD_MORE_SUCCESS);
                    }
                    goodsLiveData.setValue(goodsList);
                }, throwable -> {
                    flagLiveData.setValue(HomeViewModel.LOAD_ERROR);
                });
        compositeDisposable.add(disposable);
    }

    public void getTotalCount() {
        Disposable disposable = goodsService.getRecordsFiltered("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    totalCountLiveData.setValue(responseResult.getData());
                }, throwable -> {
                    flagLiveData.setValue(HomeViewModel.ERROR);
                });
        compositeDisposable.add(disposable);
    }

    public void getSearchList(String searchValue, Integer currentPage, Integer pageSize, boolean reFresh) {
        Disposable disposable = goodsService.searchGoodsList(searchValue, currentPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    if (reFresh) {
                        searchList.clear();
                        searchList = responseResult.getData();
                        flagLiveData.setValue(HomeViewModel.REFRESH_SUCCESS);
                    } else {
                        searchList.addAll(responseResult.getData());
                        flagLiveData.setValue(HomeViewModel.LOAD_MORE_SUCCESS);
                    }
                    searchLiveData.setValue(searchList);
                }, throwable -> {
                    flagLiveData.setValue(HomeViewModel.LOAD_ERROR);
                });
        compositeDisposable.add(disposable);
    }

    public void getRecordsFiltered(String searchValue) {
        Disposable disposable = goodsService.getRecordsFiltered(searchValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    searchCountLiveData.setValue(responseResult.getData());
                }, throwable -> {
                    flagLiveData.setValue(HomeViewModel.ERROR);
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