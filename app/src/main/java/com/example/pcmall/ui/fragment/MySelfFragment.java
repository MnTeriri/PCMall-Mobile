package com.example.pcmall.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.R;
import com.example.pcmall.activity.LoginActivity;
import com.example.pcmall.activity.OrderActivity;
import com.example.pcmall.databinding.FragmentMyselfBinding;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.MySelfViewModel;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MySelfFragment extends Fragment {
    private final String TAG = "MySelfFragment";
    @Inject
    public SharedPreferences sharedPreferences;

    private FragmentMyselfBinding binding;
    private FragmentActivity activity;

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MySelfViewModel mySelfViewModel =
                new ViewModelProvider(this).get(MySelfViewModel.class);
        binding = FragmentMyselfBinding.inflate(inflater, container, false);
        activity = getActivity();

        BottomNavigationView orderNavigation = binding.orderNavigation;

//        BadgeDrawable badgeDrawable = orderNavigation.getOrCreateBadge(R.id.navigation_pay);
//        badgeDrawable.setNumber(5);
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
            Log.d(TAG, "准备启动LoginActivity");
            return true;
        });


        MaterialCardView orderCard = binding.orderCard;
        orderCard.setOnClickListener(view -> {
            Intent intent = new Intent(activity, OrderActivity.class);
            intent.putExtra("tabId", 0);
            activity.startActivity(intent);
            Log.d(TAG, "准备启动OrderActivity");
        });

        Button loginButton = binding.loginButton;
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        });

        initUserData();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MySelfFragment.onStart()");
        initUserData();
    }

    private void initUserData() {
        String data = sharedPreferences.getString("data", "");
        if ("".equals(data)) {
            Log.d(TAG, "用户没登录");
            return;
        }
        User user = JSON.parseObject(data, User.class);
        binding.loginButton.setVisibility(View.GONE);
        binding.userInformationLinearLayout.setVisibility(View.VISIBLE);
        binding.userName.setText(user.getUname());
        binding.uid.setText(user.getUid());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}