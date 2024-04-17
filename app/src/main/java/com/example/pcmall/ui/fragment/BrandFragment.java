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

import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.FragmentBrandBinding;
import com.example.pcmall.databinding.FragmentOrderBinding;
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
        brandList = new ArrayList<>();
    }

    private void initView() {
    }

    private void initListener() {
    }

    private void handelObserve() {
        brandViewModel.getBrandListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Brand>>() {
            @Override
            public void onChanged(List<Brand> list) {
                Log.d(TAG, list.toString());
            }
        });
    }
}
