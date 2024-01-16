package com.example.pcmall.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.R;
import com.example.pcmall.databinding.FragmentMyselfBinding;
import com.example.pcmall.ui.viewmodel.NotificationsViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MySelfFragment extends Fragment {

    private FragmentMyselfBinding binding;

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        binding = FragmentMyselfBinding.inflate(inflater, container, false);

        BottomNavigationView orderNavigation = binding.orderNavigation;

        BadgeDrawable badgeDrawable = orderNavigation.getOrCreateBadge(R.id.navigation_pay);
        System.out.println(badgeDrawable);
        badgeDrawable.setNumber(5);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}