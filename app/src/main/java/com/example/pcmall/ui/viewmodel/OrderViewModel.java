package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Address;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.Order;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.AddressService;
import com.example.pcmall.service.CartService;
import com.example.pcmall.service.OrderService;
import com.example.pcmall.utils.RetrofitUtils;

import java.math.BigDecimal;
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
public class OrderViewModel extends ViewModel {
    private final String TAG = "OrderViewModel";
    public final static Integer ERROR = -2;//错误
    public final static Integer LOAD_ERROR = -1;//加载错误
    public final static Integer LOAD_MORE_SUCCESS = 1;//加载更多成功
    public final static Integer REFRESH_SUCCESS = 2;//刷新成功
    public final static Integer CREATE_SUCCESS = 3;//订单创建成功
    public final static Integer PAY_SUCCESS = 4;//订单付款成功
    private final AddressService addressService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CompositeDisposable compositeDisposable;
    private List<Cart> cartList;
    private List<Order> orderList;

    @Getter
    private final MutableLiveData<List<Cart>> cartListLiveData;//创建订单界面购物车数据
    @Getter
    private final MutableLiveData<Integer> selectItemCountLiveData;//创建订单界面选择商品总件数
    @Getter
    private final MutableLiveData<BigDecimal> selectItemTotalPriceLiveData;//创建订单界面选择商品总价格
    @Getter
    private final MutableLiveData<Address> addressLiveData;//创建订单界面默认地址信息
    @Getter
    private final MutableLiveData<List<Order>> orderListLiveData;//订单界面订单数据
    @Getter
    private final MutableLiveData<Long> searchCountLiveData;//订单界面订单数据查询总数量
    @Getter
    private String oid = "";//创建订单的订单号
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public OrderViewModel(AddressService addressService, CartService cartService, OrderService orderService) {
        this.addressService = addressService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.compositeDisposable = new CompositeDisposable();
        this.cartList = new ArrayList<>();
        this.orderList = new ArrayList<>();
        this.cartListLiveData = new MutableLiveData<>();
        this.selectItemCountLiveData = new MutableLiveData<>();
        this.selectItemTotalPriceLiveData = new MutableLiveData<>();
        this.addressLiveData = new MutableLiveData<>();
        this.orderListLiveData = new MutableLiveData<>();
        this.searchCountLiveData = new MutableLiveData<>();
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
                    cartList = responseResult.getData();
                    cartListLiveData.setValue(cartList);
                    selectItemTotalPriceHandel();
                    selectItemCountHandel();
                }, throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode()));
        compositeDisposable.add(disposable);
    }

    private void selectItemTotalPriceHandel() {
        BigDecimal total = new BigDecimal("0");
        for (Cart cart : cartList) {
            Goods goods = cart.getGoods();
            if (cart.getIsSelect() == 1 && goods.getStatus() == 0 && goods.getIsDelete() == 0) {
                total = total.add(goods.getPrice().multiply(BigDecimal.valueOf(cart.getCount())));
            }
        }
        selectItemTotalPriceLiveData.setValue(total);
    }

    private void selectItemCountHandel() {
        int total = 0;
        for (Cart cart : cartList) {
            Goods goods = cart.getGoods();
            if (cart.getIsSelect() == 1 && goods.getStatus() == 0 && goods.getIsDelete() == 0) {
                total = total + cart.getCount();
            }
        }
        selectItemCountLiveData.setValue(total);
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

    public void searchOrderList(String searchValue, String uid, Integer type, Integer currentPage, Integer pageSize, boolean reFresh) {
        Disposable disposable = orderService.searchOrderList(searchValue, uid, type, currentPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    if (reFresh) {
                        orderList = responseResult.getData();
                        flagLiveData.setValue(REFRESH_SUCCESS);
                    } else {
                        orderList.addAll(responseResult.getData());
                        flagLiveData.setValue(LOAD_MORE_SUCCESS);
                    }
                    orderListLiveData.setValue(orderList);
                }, throwable -> {
                    flagLiveData.setValue(LOAD_ERROR);
                });
        compositeDisposable.add(disposable);
    }

    public void getRecordsFiltered(String searchValue, String uid, Integer type) {
        Disposable disposable = orderService.getRecordsFiltered(searchValue, uid, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> searchCountLiveData.setValue(responseResult.getData()),
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void createOrder(String uid, Integer aid) {
        Disposable disposable = orderService.createOrder(uid, aid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> {
                            flagLiveData.setValue(CREATE_SUCCESS);
                            oid = responseResult.getData();
                        },
                        throwable -> {
                            ResponseResult<String> message = RetrofitUtils.getErrorMessage(throwable);
                            if (message != null) {
                                flagLiveData.setValue(message.getCode());
                            }
                        });
        compositeDisposable.add(disposable);
    }

    public void payOrder(String oid) {
        Disposable disposable = orderService.payOrder(oid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(PAY_SUCCESS),
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
