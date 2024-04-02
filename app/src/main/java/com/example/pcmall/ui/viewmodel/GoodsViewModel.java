package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.CartService;
import com.example.pcmall.service.GoodsService;
import com.example.pcmall.utils.RetrofitUtils;

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
    private final GoodsService goodsService;
    private final CartService cartService;
    private final CompositeDisposable compositeDisposable;

    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public GoodsViewModel(GoodsService goodsService, CartService cartService) {
        this.goodsService = goodsService;
        this.cartService = cartService;
        this.compositeDisposable = new CompositeDisposable();
        this.flagLiveData = new MutableLiveData<>();
    }

    public void addCart(String uid, Integer gid) {
        Disposable disposable = cartService.addCart(uid, gid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(ResponseCode.OK.getCode()),
                        throwable -> {
                            ResponseResult<String> message = RetrofitUtils.getErrorMessage(throwable);
                            if (message != null) {
                                flagLiveData.setValue(message.getCode());
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
