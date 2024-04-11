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

import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentAddressUpdateBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.AddressViewModel;
import com.github.gzuliyujiang.wheelpicker.AddressPicker;
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnAddressPickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.CityEntity;
import com.github.gzuliyujiang.wheelpicker.entity.CountyEntity;
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity;


import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressUpdateDialog extends FullScreenDialog {
    private final String TAG = "AddressUpdateDialog";
    private DialogFragmentAddressUpdateBinding binding;
    private AddressViewModel addressViewModel;
    private final Address address;
    private AddressPicker addressPicker;
    private User user;

    public AddressUpdateDialog(FragmentActivity activity, Address address) {
        super(activity);
        this.address = address;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentAddressUpdateBinding.inflate(inflater, container, false);
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
        //初始化地址选择器
        addressPicker = new AddressPicker(getActivity());
        addressPicker.setAddressMode(AddressMode.PROVINCE_CITY_COUNTY);
        addressPicker.setDefaultValue(address.getProvince(), address.getCity(), address.getDistrict());

        //初始化修改界面
        binding.receiverNameInputText.setText(address.getReceiverName());
        binding.phoneInputText.setText(address.getPhone());
        binding.areaTextView.setText(address.getProvince() + " " + address.getCity() + " " + address.getDistrict());
        binding.addressInputText.setText(address.getAddressDetail());
    }

    private void initListener() {
        //点击收货地址弹出地址选择器
        binding.areaTextView.setOnClickListener(v -> addressPicker.show());

        //地址选择器确定后
        addressPicker.setOnAddressPickedListener((province, city, district) -> {
            address.setProvince(province.getName());
            address.setCity(city.getName());
            address.setDistrict(district.getName());
            binding.areaTextView.setText(address.getProvince() + " " + address.getCity() + " " + address.getDistrict());
        });

        //更新按钮
        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void handelObserve() {

    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
