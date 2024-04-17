package com.example.pcmall.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.pcmall.adapter.recyclerview.BrandListAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.FragmentBrandBinding;
import com.example.pcmall.databinding.FragmentOrderBinding;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.model.Brand;
import com.example.pcmall.model.Category;
import com.example.pcmall.ui.viewmodel.BrandViewModel;
import com.example.pcmall.ui.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BrandFragment extends Fragment {
    private final String TAG;
    private final Category category;
    private FragmentBrandBinding binding;
    private BrandViewModel brandViewModel;

    private BrandListAdapter brandListAdapter;
    private List<Brand> brandList;

    public BrandFragment(Category category) {
        this.category = category;
        this.TAG = "BrandFragment_" + category.getCname();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBrandBinding.inflate(inflater, container, false);
        brandViewModel = new ViewModelProvider(this).get(BrandViewModel.class);

        initData();
        initView();
        initListener();
        handelObserve();

        brandViewModel.getBrandList(category.getId());

        Log.d(TAG, "BrandFragment启动");
        return binding.getRoot();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        brandList = new ArrayList<>();
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        binding.categoryTextView.setText(category.getCname());

        brandListAdapter = new BrandListAdapter(brandList);
        binding.recycleView.setAdapter(brandListAdapter);
        binding.recycleView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击品牌打开相应品牌商品
        brandListAdapter.setOnClickListener((v, data) -> {
            Log.d(TAG, data.toString());
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        brandViewModel.getBrandListLiveData().observe(getViewLifecycleOwner(), list -> {
            brandList.clear();
            brandList.addAll(list);
            brandListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "BrandFragment销毁");
    }
}
