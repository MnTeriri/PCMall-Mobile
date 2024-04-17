package com.example.pcmall.adapter.viewpager2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pcmall.ui.fragment.BrandFragment;

import java.util.List;

public class BrandPagerAdapter extends FragmentStateAdapter {
    private final List<BrandFragment> fragmentList;

    public BrandPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<BrandFragment> fragmentList) {
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
