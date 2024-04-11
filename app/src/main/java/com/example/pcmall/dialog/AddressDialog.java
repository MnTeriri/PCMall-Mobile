package com.example.pcmall.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.AddressListAdapter;
import com.example.pcmall.adapter.listener.AdapterInterface;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentAddressBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.AddressViewModel;

import java.util.ArrayList;
import java.util.List;

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
        return binding.getRoot();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        addressList = new ArrayList<>();
        addressViewModel.getAddressList(user.getUid());
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        RecyclerView recycleView = binding.recycleView;
        addressListAdapter = new AddressListAdapter(addressList);
        recycleView.setAdapter(addressListAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void initListener() {
        //点击地址跳转到编辑
        addressListAdapter.setOnCartClickListener((v, address) -> {
            AddressUpdateDialog dialog = new AddressUpdateDialog(getActivity(), address);
            dialog.show();
        });

        //点击添加地址按钮
        binding.addButton.setOnClickListener(v -> {
            AddressAddDialog dialog = new AddressAddDialog(getActivity());
            dialog.show();
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        addressViewModel.getAddressLiveData().observe(getViewLifecycleOwner(), list -> {
            addressList.clear();
            addressList.addAll(list);
            addressListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
