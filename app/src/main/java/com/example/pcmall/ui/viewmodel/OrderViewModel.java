package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Address;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.AddressService;
import com.example.pcmall.service.CartService;
import com.example.pcmall.service.OrderService;

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
public class OrderViewModel extends ViewModel {
    private final String TAG = "OrderViewModel";
    private final AddressService addressService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CompositeDisposable compositeDisposable;

    @Getter
    private final MutableLiveData<List<Cart>> cartListLiveData;
    @Getter
    private final MutableLiveData<Address> addressLiveData;
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public OrderViewModel(AddressService addressService, CartService cartService, OrderService orderService) {
        this.addressService = addressService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.compositeDisposable = new CompositeDisposable();
        this.cartListLiveData = new MutableLiveData<>();
        this.addressLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        Log.d(TAG, "自动注入OrderService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void getSelectCartList(String uid) {
        Disposable disposable = cartService.searchSelectCartList(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    cartListLiveData.setValue(responseResult.getData());
                }, throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode()));
        compositeDisposable.add(disposable);
    }

    public void getDefaultAddress(String uid) {
        Disposable disposable = addressService.searchDefaultAddress(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    addressLiveData.setValue(responseResult.getData());
                }, throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode()));
        compositeDisposable.add(disposable);
    }

    public void createOrder() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁GoodsViewModel，清除compositeDisposable");
    }
}
