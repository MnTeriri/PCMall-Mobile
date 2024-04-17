package com.example.pcmall.adapter.viewpager2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pcmall.ui.fragment.OrderFragment;

import java.util.List;

public class OrderPagerAdapter extends FragmentStateAdapter {
    private final List<OrderFragment> fragmentList;

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<OrderFragment> fragmentList) {
        super(fragmentActivity);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
