package com.example.pcmall.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.databinding.FragmentOrderBinding;
import com.example.pcmall.ui.viewmodel.OrderViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;

@AndroidEntryPoint
public class OrderFragment extends Fragment {
    private final String TAG;
    public final static String TITLE_ALL = "全部";
    public final static String TITLE_PAY = "待付款";
    public final static String TITLE_SEND = "待发货";
    public final static String TITLE_DELIVER = "待收货";
    public final static String TITLE_REFUND = "退款/售后";
    public final static String TITLE_COMMENT = "待评价";

    private FragmentOrderBinding binding;
    private OrderViewModel orderViewModel;

    @Getter
    private final String title;

    public OrderFragment(String title) {
        this.title = title;
        this.TAG = "OrderFragment_" + title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        return binding.getRoot();
    }
}
