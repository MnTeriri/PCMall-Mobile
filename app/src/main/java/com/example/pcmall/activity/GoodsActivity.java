package com.example.pcmall.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.databinding.ActivityGoodsBinding;
import com.example.pcmall.model.Goods;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;
import com.example.pcmall.ui.viewmodel.GoodsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GoodsActivity extends AppCompatActivity {
    private final String TAG = "GoodsActivity";
    private ActivityGoodsBinding binding;
    private GoodsViewModel goodsViewModel;
    private Goods goods;

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
    }

    private void handelObserve() {

    }

}
