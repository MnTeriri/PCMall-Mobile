package com.example.pcmall.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.AddressListAdapter;
import com.example.pcmall.databinding.DialogFragmentAddressBinding;
import com.example.pcmall.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressDialog extends FullScreenDialog {
    private final String TAG = "AddressDialog";
    private DialogFragmentAddressBinding binding;

    private AddressListAdapter addressListAdapter;
    private List<Address> addressList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentAddressBinding.inflate(inflater, container, false);

        initData();
        initView();
        initListener();
        handelObserve();
        return binding.getRoot();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        addressList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            addressList.add(new Address());
        }
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        RecyclerView recycleView = binding.recycleView;
        addressListAdapter = new AddressListAdapter(addressList);
        recycleView.setAdapter(addressListAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void initListener() {
    }

    private void handelObserve() {
    }

    public void show(FragmentActivity activity) {
        super.show(activity, TAG);
    }
}
