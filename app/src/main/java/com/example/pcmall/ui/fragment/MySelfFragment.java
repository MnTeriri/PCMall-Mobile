package com.example.pcmall.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.R;
import com.example.pcmall.activity.LoginActivity;
import com.example.pcmall.activity.OrderActivity;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.FragmentMyselfBinding;
import com.example.pcmall.dialog.AddressDialog;
import com.example.pcmall.dialog.CartDialog;
import com.example.pcmall.dialog.MessageDialog;
import com.example.pcmall.model.User;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;
import com.example.pcmall.ui.viewmodel.MySelfViewModel;
import com.google.android.material.badge.BadgeDrawable;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MySelfFragment extends Fragment {
    private final String TAG = "MySelfFragment";
    private FragmentMyselfBinding binding;
    private MySelfViewModel mySelfViewModel;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyselfBinding.inflate(inflater, container, false);
        mySelfViewModel = new ViewModelProvider(this).get(MySelfViewModel.class);
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        initData();
        initView();
        handelObserve();
        initListener();

        Log.d(TAG, "MySelfFragment启动");
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, TAG + ".onStart()");
        Log.d(TAG, "网络请求数据。。。");
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();
        if (user != null) {
            binding.loginButton.setVisibility(View.GONE);
            binding.userInformationLinearLayout.setVisibility(View.VISIBLE);
            binding.userName.setText(user.getUname());
            binding.uid.setText(user.getUid());
            GlideApp.with(getContext())
                    .load(NetworkModule.baseUrl + "image/" + user.getImage())
                    .into(binding.userImage);

            mySelfViewModel.getNotPayCount(user.getUid());
            mySelfViewModel.getNotSendCount(user.getUid());
            mySelfViewModel.getNotDeliverCount(user.getUid());
            mySelfViewModel.getRefundCount(user.getUid());
        }
    }

    private void initData() {
        Log.d(TAG, "加载数据");
    }

    private void initView() {
        Log.d(TAG, "初始化View");
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //订单导航栏
        binding.orderNavigation.setOnItemSelectedListener(menuItem -> {
            if (user == null) {
                new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("请登录！").show();
                return true;
            }
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
            if (user == null) {
                new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("请登录！").show();
                return true;
            }
            int itemId = menuItem.getItemId();
            if (itemId == R.id.navigation_address) {
                AddressDialog addressDialog = new AddressDialog(getActivity());
                addressDialog.show();
            } else if (itemId == R.id.navigation_cart) {
                CartDialog cartDialog = new CartDialog(getActivity());
                cartDialog.show();
            }
            return true;
        });

        binding.orderCard.setOnClickListener(view -> {
            if (user == null) {
                new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("请登录！").show();
                return;
            }
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
        Log.d(TAG, "添加ViewModel返回结果方法");
        //未付款订单个数
        mySelfViewModel.getNotPayCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_pay);
            if (count != 0) {
                badgeDrawable.setNumber(count.intValue());
            } else {
                badgeDrawable.clearNumber();
            }
        });

        //未发货订单个数
        mySelfViewModel.getNotSendCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_send);
            if (count != 0) {
                badgeDrawable.setNumber(count.intValue());
            } else {
                badgeDrawable.clearNumber();
            }
        });

        //未收货订单个数
        mySelfViewModel.getNotDeliverCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_deliver);
            if (count != 0) {
                badgeDrawable.setNumber(count.intValue());
            } else {
                badgeDrawable.clearNumber();
            }
        });

        //退款订单个数
        mySelfViewModel.getRefundCountLiveData().observe(getViewLifecycleOwner(), count -> {
            BadgeDrawable badgeDrawable = binding.orderNavigation.getOrCreateBadge(R.id.navigation_refund);
            if (count != 0) {
                badgeDrawable.setNumber(count.intValue());
            } else {
                badgeDrawable.clearNumber();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "MySelfFragment销毁");
    }
}