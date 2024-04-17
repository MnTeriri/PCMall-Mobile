package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.Brand;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.service.BrandService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class BrandViewModel extends ViewModel {
    private final String TAG = "BrandViewModel";
    private final BrandService brandService;
    private final CompositeDisposable compositeDisposable;

    @Getter
    private final MutableLiveData<List<Brand>> brandListLiveData;//品牌信息
    @Getter
    private final MutableLiveData<Integer> flagLiveData;

    @Inject
    public BrandViewModel(BrandService brandService) {
        this.brandService = brandService;
        this.compositeDisposable = new CompositeDisposable();
        this.brandListLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
        Log.d(TAG, "自动注入CategoryService完成");
        Log.d(TAG, "MutableLiveData初始化完成");
    }

    public void getBrandList(Integer cid) {
        Disposable disposable = brandService.searchBrandByCid(cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseResult -> {
                    Log.d(TAG, responseResult.toString());
                    brandListLiveData.setValue(responseResult.getData());
                }, throwable -> {
                    flagLiveData.setValue(ResponseCode.ERROR.getCode());
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁BrandViewModel，清除compositeDisposable");
    }
}
