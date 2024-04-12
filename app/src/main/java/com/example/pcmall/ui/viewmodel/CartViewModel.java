package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Cart;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.CartService;

import java.math.BigDecimal;
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
    public final static Integer ERROR = -2;//错误
    public final static Integer LOAD_ERROR = -1;//加载错误
    public final static Integer LOAD_MORE_SUCCESS = 1;//加载更多成功
    public final static Integer REFRESH_SUCCESS = 2;//刷新成功
    private final CartService cartService;
    private final CompositeDisposable compositeDisposable;
    private List<Cart> cartList;

    @Getter
    private final MutableLiveData<List<Cart>> cartLiveData;
    @Getter
    private final MutableLiveData<BigDecimal> totalPriceLiveData;
    @Getter
    private final MutableLiveData<Long> totalCountLiveData;
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public CartViewModel(CartService cartService) {
        this.cartService = cartService;
        this.compositeDisposable = new CompositeDisposable();
        this.cartLiveData = new MutableLiveData<>();
        this.totalPriceLiveData = new MutableLiveData<>();
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
                    selectPriceHandel();
                }, throwable -> {
                    flagLiveData.setValue(CartViewModel.LOAD_ERROR);
                });
        compositeDisposable.add(disposable);
    }

    private void selectPriceHandel() {
        BigDecimal total = new BigDecimal("0");
        for (Cart cart : cartList) {
            Goods goods = cart.getGoods();
            if (cart.getIsSelect() == 1 && goods.getStatus() == 0 && goods.getIsDelete() == 0) {
                total = total.add(goods.getPrice().multiply(BigDecimal.valueOf(cart.getCount())));
            }
        }
        totalPriceLiveData.setValue(total);
    }

    public void getTotalCount(String uid) {
        Disposable disposable = cartService.getTotalCount(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> totalCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void addCartCount(Integer id) {
        Disposable disposable = cartService.addCartCount(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(ResponseCode.OK.getCode()),
                        throwable -> flagLiveData.setValue(ResponseCode.GOODS_NOT_ENOUGH_ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void subCartCount(Integer id) {
        Disposable disposable = cartService.subCartCount(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(ResponseCode.OK.getCode()),
                        throwable -> flagLiveData.setValue(ResponseCode.CART_MIN_COUNT_ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void selectCart(Integer id, Integer isSelect) {
        Disposable disposable = cartService.selectCart(id, isSelect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(ResponseCode.OK.getCode()),
                        throwable -> flagLiveData.setValue(ResponseCode.CART_GOODS_ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void selectAllCart(String uid, Integer isSelect) {
        Disposable disposable = cartService.selectAllCart(uid, isSelect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(ResponseCode.OK.getCode()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void deleteCart(Integer id) {
        Disposable disposable = cartService.deleteCart(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(ResponseCode.OK.getCode()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode()));
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁CartViewModel，清除compositeDisposable");
    }
}
