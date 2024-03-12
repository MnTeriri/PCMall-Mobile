package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Cart;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.CartService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;

@HiltViewModel
public class CartViewModel extends ViewModel {
    private final String TAG = "CartViewModel";
    public final static Integer ERROR = -2;
    public final static Integer LOAD_ERROR = -1;
    public final static Integer LOAD_MORE_SUCCESS = 1;
    public final static Integer REFRESH_SUCCESS = 2;
    private final CartService cartService;
    private final CompositeDisposable compositeDisposable;
    private List<Cart> cartList;

    @Getter
    private final MutableLiveData<List<Cart>> cartLiveData;
    @Getter
    private final MutableLiveData<Long> totalCountLiveData;
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public CartViewModel(CartService cartService) {
        this.cartService = cartService;
        this.compositeDisposable = new CompositeDisposable();
        this.cartLiveData = new MutableLiveData<>();
        this.totalCountLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        this.cartList = new ArrayList<>();
        Log.d(TAG, "自动注入CartService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void getCartList(String uid, Integer currentPage, Integer pageSize, boolean reFresh) {
        Disposable disposable = cartService.searchCartList(uid, currentPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    if (reFresh) {
                        cartList.clear();
                        cartList = responseResult.getData();
                        flagLiveData.setValue(CartViewModel.REFRESH_SUCCESS);
                    } else {
                        cartList.addAll(responseResult.getData());
                        flagLiveData.setValue(CartViewModel.LOAD_MORE_SUCCESS);
                    }
                    cartLiveData.setValue(cartList);
                }, throwable -> {
                    flagLiveData.setValue(CartViewModel.LOAD_ERROR);
                });
        compositeDisposable.add(disposable);
    }

    public void getTotalCount(String uid) {
        Disposable disposable = cartService.getTotalCount(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    totalCountLiveData.setValue(responseResult.getData());
                }, throwable -> {
                    flagLiveData.setValue(CartViewModel.ERROR);
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
