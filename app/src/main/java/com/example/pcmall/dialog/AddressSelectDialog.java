package com.example.pcmall.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.AddressSelectListAdapter;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentAddressSelectBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.AddressViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressSelectDialog extends BottomSheetDialogFragment {
    private final String TAG = "AddressSelectDialog";
    private DialogFragmentAddressSelectBinding binding;
    private final FragmentActivity activity;
    private ListenerInterface.OnDialogClosedReturnDataListener<Address> closeListener;
    private AddressViewModel addressViewModel;
    private User user;

    private AddressSelectListAdapter addressSelectListAdapter;
    private List<Address> addressList;
    private Address selectAddress;

    public AddressSelectDialog(FragmentActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFragmentAddressSelectBinding.inflate(inflater, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置Dialog运行状态
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) getView().getParent());
        bottomSheetBehavior.setHideable(false);//不能下滑关闭
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        addressList = new ArrayList<>();
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        RecyclerView recycleView = binding.recycleView;
        addressSelectListAdapter = new AddressSelectListAdapter(addressList);
        recycleView.setAdapter(addressSelectListAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void initListener() {
        //点击添加地址按钮
        binding.addButton.setOnClickListener(v -> {
            AddressAddDialog dialog = new AddressAddDialog(getActivity());
            dialog.show();
            dialog.setOnDialogClosedListener(dialogFragment -> {
                Log.d(TAG, dialogFragment.getTag() + "关闭！");
                addressViewModel.getAddressList(user.getUid());
            });
        });

        //点击编辑地址按钮
        addressSelectListAdapter.setEditListener((v, data) -> {
            AddressUpdateDialog dialog = new AddressUpdateDialog(getActivity(), data);
            dialog.show();
            dialog.setOnDialogClosedListener(dialogFragment -> {
                Log.d(TAG, dialogFragment.getTag() + "关闭！");
                addressViewModel.getAddressList(user.getUid());
            });
        });

        //点击选择地址按钮
        addressSelectListAdapter.setSelectListener((v, data) -> {
            selectAddress = data;
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        addressViewModel.getAddressListLiveData().observe(getViewLifecycleOwner(), list -> {
            addressList.clear();
            addressList.addAll(list);
            addressSelectListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (closeListener != null) {
            closeListener.onDialogClosed(this, selectAddress);
        }
    }

    public void setOnDialogClosedListener(ListenerInterface.OnDialogClosedReturnDataListener<Address> listener) {
        this.closeListener = listener;
    }

    public void show() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        this.show(fragmentManager, TAG);
    }
}
