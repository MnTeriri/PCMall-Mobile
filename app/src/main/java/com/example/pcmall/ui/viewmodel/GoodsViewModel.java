package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Goods;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.CartService;
import com.example.pcmall.service.GoodsService;
import com.example.pcmall.utils.RetrofitUtils;

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
    public final static Integer LOAD_ERROR = -1;//加载错误
    public final static Integer LOAD_MORE_SUCCESS = 1;//加载更多成功
    public final static Integer REFRESH_SUCCESS = 2;//刷新成功
    private final GoodsService goodsService;
    private final CartService cartService;
    private final CompositeDisposable compositeDisposable;
    private List<Goods> goodsList;

    @Getter
    private final MutableLiveData<List<Goods>> goodsListLiveData;//商品信息（cid、bid筛选）
    @Getter
    private final MutableLiveData<Long> totalCountLiveData;///商品信息总数（cid、bid筛选）
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public GoodsViewModel(GoodsService goodsService, CartService cartService) {
        this.goodsService = goodsService;
        this.cartService = cartService;
        this.compositeDisposable = new CompositeDisposable();
        this.goodsListLiveData = new MutableLiveData<>();
        this.totalCountLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        this.goodsList = new ArrayList<>();
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

    public void searchGoodsByCidAndBid(Integer cid, Integer bid, Integer currentPage, Integer pageSize, boolean reFresh) {
        Disposable disposable = goodsService.searchGoodsByCidAndBid(cid, bid, currentPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    if (reFresh) {
                        goodsList = responseResult.getData();
                        flagLiveData.setValue(CartViewModel.REFRESH_SUCCESS);
                    } else {
                        goodsList.addAll(responseResult.getData());
                        flagLiveData.setValue(CartViewModel.LOAD_MORE_SUCCESS);
                    }
                    goodsListLiveData.setValue(goodsList);
                }, throwable -> {
                    flagLiveData.setValue(CartViewModel.LOAD_ERROR);
                });
        compositeDisposable.add(disposable);
    }

    public void getRecordsFilteredByCidAndBid(Integer cid, Integer bid) {
        Disposable disposable = goodsService.getRecordsFilteredByCidAndBid(cid, bid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> totalCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
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
