package com.example.pcmall.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.ActivityGoodsBinding;
import com.example.pcmall.dialog.MessageDialog;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;
import com.example.pcmall.ui.viewmodel.GoodsViewModel;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.SneakyThrows;

@AndroidEntryPoint
public class GoodsActivity extends AppCompatActivity {
    private final String TAG = "GoodsActivity";
    private ActivityGoodsBinding binding;
    private GoodsViewModel goodsViewModel;
    private Goods goods;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoodsBinding.inflate(getLayoutInflater());
        goodsViewModel = new ViewModelProvider(this).get(GoodsViewModel.class);
        setContentView(binding.getRoot());

        initData();
        initView();
        initListener();
        handelObserve();
    }

    private void initData() {
        goods = JSON.parseObject(getIntent().getStringExtra("goods"), Goods.class);
        user = ((PCMallApplication) getApplication()).getUserData();
    }

    private void initView() {
        GlideApp.with(this)
                .load(NetworkModule.baseUrl + "image/" + goods.getImage())
                .into(binding.imageView);
        binding.gnameTextView.setText(goods.getBrand().getBname() + " " + goods.getGname());
        binding.descriptionTextView.setText(goods.getDescription());
        binding.priceTextView.setText("￥" + goods.getPrice().toString());
    }

    private void initListener() {
        //点击topAppBar的按钮返回到MainActivity
        binding.topAppBar.setNavigationOnClickListener(v -> finish());

        //添加购物车button
        binding.addCartButton.setOnClickListener(v -> {
            if (user != null) {
                goodsViewModel.addCart(user.getUid(), goods.getId());
            } else {
                SweetAlertDialog alertDialog = new SweetAlertDialog(GoodsActivity.this, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText("未登录!");
                alertDialog.show();
                alertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        alertDialog.dismissWithAnimation();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        });
    }

    private void handelObserve() {
        goodsViewModel.getFlagLiveData().observe(this, integer -> {
            if (Objects.equals(integer, ResponseCode.OK.getCode())) {
                MessageDialog alertDialog = new MessageDialog(GoodsActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                alertDialog.setTitleText("添加购物车成功");
                alertDialog.show();
            } else if (Objects.equals(integer, ResponseCode.CART_GOODS_ERROR.getCode())) {
                MessageDialog alertDialog = new MessageDialog(GoodsActivity.this, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText("购物车商品状态异常");
                alertDialog.show();
            } else if (Objects.equals(integer, ResponseCode.GOODS_OFF_SHELF_ERROR.getCode())) {
                MessageDialog alertDialog = new MessageDialog(GoodsActivity.this, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText("商品下架");
                alertDialog.show();
            } else if (Objects.equals(integer, ResponseCode.GOODS_NOT_ENOUGH_ERROR.getCode())) {
                MessageDialog alertDialog = new MessageDialog(GoodsActivity.this, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText("商品缺货");
                alertDialog.show();
            }
        });
    }

}
