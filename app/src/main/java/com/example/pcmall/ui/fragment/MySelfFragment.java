package com.example.pcmall.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.R;
import com.example.pcmall.activity.LoginActivity;
import com.example.pcmall.activity.OrderActivity;
import com.example.pcmall.databinding.FragmentMyselfBinding;
import com.example.pcmall.ui.viewmodel.NotificationsViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MySelfFragment extends Fragment {

    private FragmentMyselfBinding binding;

    private FragmentActivity activity;

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        binding = FragmentMyselfBinding.inflate(inflater, container, false);
        activity = getActivity();

        BottomNavigationView orderNavigation = binding.orderNavigation;

        BadgeDrawable badgeDrawable = orderNavigation.getOrCreateBadge(R.id.navigation_pay);
        badgeDrawable.setNumber(5);
        orderNavigation.setOnItemSelectedListener(menuItem -> {
            Intent intent = new Intent(activity, OrderActivity.class);
            int itemId = menuItem.getItemId();
            if (itemId == R.id.navigation_pay) {
                intent.putExtra("tabId", 1);
            } else if (itemId == R.id.navigation_send) {
                intent.putExtra("tabId", 2);
            } else if (itemId == R.id.navigation_deliver) {
                intent.putExtra("tabId", 3);
            } else if (itemId == R.id.navigation_refund) {
                intent.putExtra("tabId", 4);
            } else if (itemId == R.id.navigation_comment) {
                intent.putExtra("tabId", 5);
            } else {
                return false;
            }
            activity.startActivity(intent);
            Log.d("orderNavigation", "进入OrderActivity");
            return true;
        });


        MaterialCardView orderCard = binding.orderCard;
        orderCard.setOnClickListener(view -> {
            Intent intent = new Intent(activity, OrderActivity.class);
            intent.putExtra("tabId", 0);
            activity.startActivity(intent);
            Log.d("orderCard", "进入OrderActivity");
        });

        Button loginButton = binding.loginButton;
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}