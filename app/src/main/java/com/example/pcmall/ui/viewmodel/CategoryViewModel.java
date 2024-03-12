package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.pcmall.service.CategoryService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@HiltViewModel
public class CategoryViewModel extends ViewModel {
    private final String TAG = "CategoryViewModel";
    private final CategoryService categoryService;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public CategoryViewModel(CategoryService categoryService) {
        this.categoryService = categoryService;
        this.compositeDisposable = new CompositeDisposable();
        Log.d(TAG, "自动注入CategoryService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁GoodsViewModel，清除compositeDisposable");
    }
}