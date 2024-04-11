package com.example.pcmall.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentAddressAddBinding;
import com.example.pcmall.databinding.DialogFragmentAddressUpdateBinding;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.AddressViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressAddDialog extends FullScreenDialog {
    private final String TAG = "AddressAddDialog";
    private DialogFragmentAddressAddBinding binding;
    private AddressViewModel addressViewModel;
    private User user;

    public AddressAddDialog(FragmentActivity activity) {
        super(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentAddressAddBinding.inflate(inflater, container, false);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        initData();
        initView();
        initListener();
        handelObserve();
        return binding.getRoot();
    }

    private void initData() {
    }

    private void initView() {
    }

    private void initListener() {
    }

    private void handelObserve() {
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
