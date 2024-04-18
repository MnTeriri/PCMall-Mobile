package com.example.pcmall.dialog;

import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.recyclerview.OrderCartListAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentOrderCreateBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateOrderDialog extends FullScreenDialog {
    private final String TAG = "CreateOrderDialog";
    private DialogFragmentOrderCreateBinding binding;
    private OrderViewModel orderViewModel;

    private User user;
    private OrderCartListAdapter orderCartListAdapter;
    private List<Cart> cartList;
    private Address selectAddress;

    public CreateOrderDialog(FragmentActivity activity) {
        super(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentOrderCreateBinding.inflate(inflater, container, false);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        initData();
        initView();
        initListener();
        handelObserve();

        //请求数据
        orderViewModel.getSelectCartList(user.getUid());
        orderViewModel.getDefaultAddress(user.getUid());

        return binding.getRoot();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        cartList = new ArrayList<>();
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        RecyclerView recycleView = binding.recycleView;
        orderCartListAdapter = new OrderCartListAdapter(cartList);
        recycleView.setAdapter(orderCartListAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击topAppBar的按钮返回
        binding.topAppBar.setNavigationOnClickListener(v -> dismiss());

        //点击地址卡片弹出选择地址窗口
        binding.addressInformationCard.setOnClickListener(v -> {
            AddressSelectDialog dialog = new AddressSelectDialog(getActivity(), selectAddress);
            dialog.setOnDialogClosedListener((dialogFragment, address) -> {
                selectAddress = address;
                if (address != null) {
                    binding.addressLayout.setVisibility(View.VISIBLE);
                    binding.warningLayout.setVisibility(View.GONE);
                    binding.areaTextView.setText(address.getProvince() + address.getCity() + address.getDistrict());
                    binding.addressTextView.setText(address.getAddressDetail());
                    binding.receiverTextView.setText(address.getReceiverName() + " " + address.getPhone());
                } else {
                    binding.addressLayout.setVisibility(View.GONE);
                    binding.warningLayout.setVisibility(View.VISIBLE);
                }
            });
            dialog.show();
        });

        //点击创建订单按钮
        binding.createButton.setOnClickListener(v -> {
            if (selectAddress != null) {
                orderViewModel.createOrder(user.getUid(), selectAddress.getId());
            } else {
                Toast.makeText(getContext(), "没选择地址！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        //加载选中购物车数据
        orderViewModel.getCartListLiveData().observe(getViewLifecycleOwner(), list -> {
            cartList.clear();
            cartList.addAll(list);
            orderCartListAdapter.notifyDataSetChanged();
        });

        //加载默认地址数据
        orderViewModel.getAddressLiveData().observe(getViewLifecycleOwner(), address -> {
            selectAddress = address;
            if (address != null) {
                binding.addressLayout.setVisibility(View.VISIBLE);
                binding.warningLayout.setVisibility(View.GONE);
                binding.areaTextView.setText(address.getProvince() + address.getCity() + address.getDistrict());
                binding.addressTextView.setText(address.getAddressDetail());
                binding.receiverTextView.setText(address.getReceiverName() + " " + address.getPhone());
            } else {
                binding.addressLayout.setVisibility(View.GONE);
                binding.warningLayout.setVisibility(View.VISIBLE);
            }
        });

        //加载总价格
        orderViewModel.getSelectItemTotalPriceLiveData().observe(getViewLifecycleOwner(), totalPrice -> {
            binding.priceTextView.setText("￥" + totalPrice);
            binding.totalPriceTextView.setText("￥" + totalPrice);
        });

        //加载商品总件数
        orderViewModel.getSelectItemCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            binding.countTextView.setText(totalCount.toString());
            binding.totalCountTextView.setText(totalCount.toString());
        });

        orderViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, OrderViewModel.CREATE_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("创建订单成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> showPayDialog());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, OrderViewModel.PAY_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("订单付款成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, ResponseCode.GOODS_NOT_ENOUGH_ERROR.getCode())) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("商品缺货！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, ResponseCode.CART_GOODS_ERROR.getCode())) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("购物车商品状态异常！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, ResponseCode.CART_EMPTY_ERROR.getCode())) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("购物车为空！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, ResponseCode.ERROR.getCode())) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.ERROR_TYPE).setTitleText("错误！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            }
        });
    }

    private void showPayDialog() {
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("付款界面")
                .setContentText("这是一个付款界面，如需付款点击付款按钮")
                .setConfirmText("付款")
                .setConfirmClickListener(dialog -> {
                    orderViewModel.payOrder(orderViewModel.getOid());
                    dialog.dismissWithAnimation();
                })
                .setCancelText("取消")
                .setCancelClickListener(dialog -> {
                    dialog.dismissWithAnimation();
                    dismiss();
                })
                .show();
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
