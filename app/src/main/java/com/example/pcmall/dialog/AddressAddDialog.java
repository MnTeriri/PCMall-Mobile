package com.example.pcmall.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentAddressAddBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.AddressViewModel;
import com.github.gzuliyujiang.wheelpicker.AddressPicker;
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressAddDialog extends FullScreenDialog {
    private final String TAG = "AddressAddDialog";
    private DialogFragmentAddressAddBinding binding;
    private AddressViewModel addressViewModel;
    private User user;

    private Address address;
    private AddressPicker addressPicker;

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
        Log.d(TAG, "加载数据");
        address = new Address();
        address.setUid(user.getUid());
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        //初始化地址选择器
        addressPicker = new AddressPicker(getActivity());
        addressPicker.setAddressMode(AddressMode.PROVINCE_CITY_COUNTY);
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击topAppBar的按钮返回
        binding.topAppBar.setNavigationOnClickListener(v -> dismiss());

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
        binding.addButton.setOnClickListener(v -> {
            String receiverName = binding.receiverNameInputText.getText().toString();
            String phone = binding.phoneInputText.getText().toString();
            String addressDetail = binding.addressDetailInputText.getText().toString();
            Integer isDefault = ((MaterialCheckBox) binding.isDefaultCheckBox).getCheckedState();
            //做违法判断，弹窗后return。。。


            //更新
            address.setReceiverName(receiverName)
                    .setPhone(phone)
                    .setAddressDetail(addressDetail)
                    .setIsDefault(isDefault);
            addressViewModel.addAddress(address);
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        addressViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, ResponseCode.OK.getCode())) {
                new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("添加地址成功！").show();
                new Thread(() -> {
                    try {
                        Thread.sleep(2500);
                        dismiss();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            } else if (Objects.equals(flag, ResponseCode.ERROR.getCode())) {
                Toast.makeText(getContext(), "错误！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
