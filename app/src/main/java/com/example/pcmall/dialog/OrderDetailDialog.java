package com.example.pcmall.dialog;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pcmall.R;
import com.example.pcmall.adapter.recyclerview.OrderCartListAdapter;
import com.example.pcmall.adapter.recyclerview.OrderGoodsListAdapter;
import com.example.pcmall.adapter.recyclerview.OrderListAdapter;
import com.example.pcmall.databinding.DialogFragmentOrderDetailBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.Order;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.OrderViewModel;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderDetailDialog extends FullScreenDialog {
    private final String TAG = "OrderDetailDialog";
    private DialogFragmentOrderDetailBinding binding;
    private OrderViewModel orderViewModel;
    private final Order order;

    public OrderDetailDialog(FragmentActivity activity, Order order) {
        super(activity);
        this.order = order;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentOrderDetailBinding.inflate(inflater, container, false);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initData();
        initView();
        initListener();
        handelObserve();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, TAG + ".onStart()");
    }

    private void initData() {
        Log.d(TAG, "加载数据");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() {
        Log.d(TAG, "初始化View");
        binding.recycleView.setAdapter(new OrderGoodsListAdapter(order.getGoodsList()));
        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        Address address = order.getAddress();
        binding.areaTextView.setText(address.getProvince() + address.getCity() + address.getDistrict());
        binding.addressTextView.setText(address.getAddressDetail());
        binding.receiverTextView.setText(address.getReceiverName() + " " + address.getPhone());

        binding.priceTextView.setText(String.format(getString(R.string.price), order.getPrice()));
        new Thread(() -> {
            Integer count = 0;
            for (Goods goods : order.getGoodsList()) {
                count += goods.getCount();
            }
            Integer finalCount = count;
            getActivity().runOnUiThread(() -> binding.countTextView.setText(finalCount.toString()));
        }).start();
        binding.statusTextView.setText(OrderListAdapter.STATUS[order.getStatus()]);
        binding.oidTextView.setText(order.getOid());
        binding.createdTimeTextView.setText(order.getCreatedTime() != null ? order.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "---");
        binding.payTimeTextView.setText(order.getPayTime() != null ? order.getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "---");
        binding.sendTimeTextView.setText(order.getSendTime() != null ? order.getSendTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "---");
        binding.finishTimeTextView.setText(order.getFinishTime() != null ? order.getFinishTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "---");

        if (order.getStatus() == 0) {
            binding.buttonLayout.setVisibility(View.VISIBLE);
            binding.payButton.setVisibility(View.VISIBLE);
            binding.finishButton.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.VISIBLE);
            binding.refundButton.setVisibility(View.GONE);
        } else if (order.getStatus() == 1) {
            binding.buttonLayout.setVisibility(View.VISIBLE);
            binding.payButton.setVisibility(View.GONE);
            binding.finishButton.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.VISIBLE);
            binding.refundButton.setVisibility(View.GONE);
        } else if (order.getStatus() == 2) {
            binding.buttonLayout.setVisibility(View.VISIBLE);
            binding.payButton.setVisibility(View.GONE);
            binding.finishButton.setVisibility(View.VISIBLE);
            binding.cancelButton.setVisibility(View.GONE);
            binding.refundButton.setVisibility(View.VISIBLE);
        } else if (order.getStatus() == 3) {
            binding.buttonLayout.setVisibility(View.VISIBLE);
            binding.payButton.setVisibility(View.GONE);
            binding.finishButton.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.GONE);
            binding.refundButton.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击topAppBar的按钮返回
        binding.topAppBar.setNavigationOnClickListener(v -> dismiss());

        //点击付款按钮
        binding.payButton.setOnClickListener(v ->
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("付款界面")
                        .setContentText("这是一个付款界面，如需付款点击付款按钮")
                        .setConfirmText("付款")
                        .setConfirmClickListener(dialog -> {
                            orderViewModel.payOrder(order.getOid());
                            dialog.dismissWithAnimation();
                        })
                        .setCancelText("取消")
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show());

        //点击确认签收按钮
        binding.finishButton.setOnClickListener(v ->
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确认界面")
                        .setContentText("确认签收？")
                        .setConfirmText("确定")
                        .setConfirmClickListener(dialog -> {
                            orderViewModel.finishOrder(order.getOid());
                            dialog.dismissWithAnimation();
                        })
                        .setCancelText("取消")
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show());

        //点击取消订单按钮
        binding.cancelButton.setOnClickListener(v ->
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("取消订单界面")
                        .setContentText("是否取消该订单")
                        .setConfirmText("确认")
                        .setConfirmClickListener(dialog -> {
                            orderViewModel.cancelOrder(order.getOid());
                            dialog.dismissWithAnimation();
                        })
                        .setCancelText("取消")
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show());

        //点击退货按钮
        binding.refundButton.setOnClickListener(v ->
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("申请退货界面")
                        .setContentText("是否申请退货")
                        .setConfirmText("确认")
                        .setConfirmClickListener(dialog -> {
                            orderViewModel.refundOrder(order.getOid());
                            dialog.dismissWithAnimation();
                        })
                        .setCancelText("取消")
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show());
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        orderViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, OrderViewModel.PAY_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("订单付款成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, OrderViewModel.FINISH_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("订单签收成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, OrderViewModel.CANCEL_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("订单取消成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, OrderViewModel.REFUND_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("申请退货成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
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
