package com.example.pcmall.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.R;
import com.example.pcmall.activity.LoginActivity;
import com.example.pcmall.activity.OrderActivity;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.FragmentMyselfBinding;
import com.example.pcmall.dialog.AddressDialog;
import com.example.pcmall.dialog.CartDialog;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.MySelfViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.navigation.NavigationBarView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MySelfFragment extends Fragment {
    private final String TAG = "MySelfFragment";

    private MySelfViewModel mySelfViewModel;
    private User user;
    private FragmentMyselfBinding binding;

    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyselfBinding.inflate(inflater, container, false);
        mySelfViewModel = new ViewModelProvider(this).get(MySelfViewModel.class);
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        if (user != null) {
            initData();
            initView();
            handelObserve();
        }
        initListener();

        return binding.getRoot();
    }

    private void initData() {
        mySelfViewModel.getNotPayCount(user.getUid());
        mySelfViewModel.getNotSendCount(user.getUid());
        mySelfViewModel.getNotDeliverCount(user.getUid());
        mySelfViewModel.getRefundCount(user.getUid());
    }

    private void initView() {

    }

    private void initListener() {
        //订单导航栏
        binding.orderNavigation.setOnItemSelectedListener(menuItem -> {
            Intent intent = new Intent(getActivity(), OrderActivity.class);
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
            getActivity().startActivity(intent);
            Log.d(TAG, "准备启动OrderActivity");
            return true;
        });

        //服务导航栏
        binding.serviceNavigation.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.navigation_address) {
                AddressDialog addressDialog = new AddressDialog();
                addressDialog.show(getActivity());
            } else if (itemId == R.id.navigation_cart) {
                CartDialog cartDialog = new CartDialog();
                cartDialog.show(getActivity());
            }
            return true;
        });

        binding.orderCard.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), OrderActivity.class);
            intent.putExtra("tabId", 0);
            getActivity().startActivity(intent);
            Log.d(TAG, "准备启动OrderActivity");
        });

        binding.loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
        });

    }

    private void handelObserve() {
        //未付款订单个数
        mySelfViewModel.getNotPayCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_pay);
            badgeDrawable.setNumber(count.intValue());
        });

        //未发货订单个数
        mySelfViewModel.getNotSendCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_send);
            badgeDrawable.setNumber(count.intValue());
        });

        //未收货订单个数
        mySelfViewModel.getNotDeliverCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_deliver);
            badgeDrawable.setNumber(count.intValue());
        });

        //退款订单个数
        mySelfViewModel.getRefundCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_refund);
            badgeDrawable.setNumber(count.intValue());
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MySelfFragment.onStart()");
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();
        if (user != null) {
            binding.loginButton.setVisibility(View.GONE);
            binding.userInformationLinearLayout.setVisibility(View.VISIBLE);
            binding.userName.setText(user.getUname());
            binding.uid.setText(user.getUid());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}