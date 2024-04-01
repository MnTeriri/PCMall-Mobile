package com.example.pcmall.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.pcmall.service.GoodsService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GoodsViewModel extends ViewModel {
    private GoodsService goodsService;

    @Inject
    public GoodsViewModel(GoodsService goodsService) {
        this.goodsService = goodsService;
    }
}
