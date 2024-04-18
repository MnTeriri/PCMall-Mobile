package com.example.pcmall.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.recyclerview.AddressListAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentAddressBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.AddressViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressDialog extends FullScreenDialog {
    private final String TAG = "AddressDialog";
    private DialogFragmentAddressBinding binding;
    private AddressViewModel addressViewModel;
    private User user;

    private AddressListAdapter addressListAdapter;
    private List<Address> addressList;

    public AddressDialog(FragmentActivity activity) {
        super(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentAddressBinding.inflate(inflater, container, false);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        initData();
        initView();
        initListener();
        handelObserve();

        //请求数据
        addressViewModel.getAddressList(user.getUid());

        return binding.getRoot();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        addressList = new ArrayList<>();
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        addressListAdapter = new AddressListAdapter(addressList);
        binding.recycleView.setAdapter(addressListAdapter);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击topAppBar的按钮返回
        binding.topAppBar.setNavigationOnClickListener(v -> dismiss());

        //点击地址跳转到编辑
        addressListAdapter.setOnClickListener((v, address) -> {
            AddressUpdateDialog dialog = new AddressUpdateDialog(getActivity(), address);
            dialog.show();
            dialog.setOnDialogClosedListener(dialogFragment -> {
                Log.d(TAG, dialogFragment.getTag() + "关闭！");
                addressViewModel.getAddressList(user.getUid());
            });
        });

        //点击添加地址按钮
        binding.addButton.setOnClickListener(v -> {
            AddressAddDialog dialog = new AddressAddDialog(getActivity());
            dialog.show();
            dialog.setOnDialogClosedListener(dialogFragment -> {
                Log.d(TAG, dialogFragment.getTag() + "关闭！");
                addressViewModel.getAddressList(user.getUid());
            });
        });

        //上拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            addressViewModel.getAddressList(user.getUid());
        });

    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        addressViewModel.getAddressListLiveData().observe(getViewLifecycleOwner(), list -> {
            addressList.clear();
            addressList.addAll(list);
            addressListAdapter.notifyDataSetChanged();
        });

        addressViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, AddressViewModel.REFRESH_SUCCESS)) {
                binding.refreshLayout.finishRefresh(true);
            } else if (Objects.equals(flag, AddressViewModel.LOAD_ERROR)) {
                binding.refreshLayout.finishRefresh(false);
            }
        });
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
