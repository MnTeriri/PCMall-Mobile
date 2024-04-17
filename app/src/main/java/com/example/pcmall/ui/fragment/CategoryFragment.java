package com.example.pcmall.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pcmall.adapter.tab.CategoryTabAdapter;
import com.example.pcmall.adapter.viewpager2.BrandPagerAdapter;
import com.example.pcmall.databinding.FragmentCategoryBinding;
import com.example.pcmall.model.Category;
import com.example.pcmall.ui.viewmodel.CategoryViewModel;


import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;

@AndroidEntryPoint
public class CategoryFragment extends Fragment {
    private final String TAG = "CategoryFragment";
    private FragmentCategoryBinding binding;
    private CategoryViewModel categoryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        binding = FragmentCategoryBinding.inflate(inflater, container, false);

        initData();
        initView();
        initListener();
        handelObserve();

        Log.d(TAG, "CategoryFragment启动");

        return binding.getRoot();
    }

    private void initData() {
        categoryViewModel.getCategoryList();
    }

    private void initView() {

    }

    private void initListener() {

    }

    private void handelObserve() {
        categoryViewModel.getCategoryListLiveData().observe(getViewLifecycleOwner(), list -> {
            binding.tabLayout.setTabAdapter(new CategoryTabAdapter(list));
            List<BrandFragment> fragmentList = new ArrayList<>();
            for (Category category : list) {
                fragmentList.add(new BrandFragment(category));
            }

            BrandPagerAdapter brandPagerAdapter = new BrandPagerAdapter(getActivity(), fragmentList);
            //禁用预加载
            binding.viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
            binding.viewPager.setAdapter(brandPagerAdapter);
            setupWithViewPager(binding.viewPager, binding.tabLayout);
        });
    }

    public void setupWithViewPager(ViewPager2 viewPager, VerticalTabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                if (viewPager != null && viewPager.getAdapter().getItemCount() >= position) {
                    viewPager.setCurrentItem(position);
                }
            }

            @Override
            public void onTabReselected(TabView tab, int position) {
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.setTabSelected(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "CategoryFragment销毁");
    }
}