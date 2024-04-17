package com.example.pcmall.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pcmall.adapter.viewpager2.OrderPagerAdapter;
import com.example.pcmall.databinding.ActivityOrderBinding;
import com.example.pcmall.ui.fragment.OrderFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderActivity extends AppCompatActivity {
    private final String TAG = "OrderActivity";
    private ActivityOrderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initView();
        initListener();
        handelObserve();

    }

    private void initData() {

    }

    private void initView() {
        List<OrderFragment> fragmentList = new ArrayList<>();
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_ALL));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_PAY));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_SEND));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_DELIVER));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_REFUND));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_COMMENT));

        OrderPagerAdapter orderPagerAdapter = new OrderPagerAdapter(this, fragmentList);

        //禁用预加载
        binding.viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        binding.viewPager.setAdapter(orderPagerAdapter);

        new TabLayoutMediator(binding.orderTab, binding.viewPager, (tab, i) -> {
            tab.setText(((OrderFragment) fragmentList.get(i)).getTitle());
        }).attach();

        int tabId = getIntent().getIntExtra("tabId", 0);
        TabLayout orderTab = binding.orderTab;
        TabLayout.Tab tabAt = orderTab.getTabAt(tabId);
        tabAt.select();
    }

    private void initListener() {
    }

    private void handelObserve() {
    }
}