package com.example.pcmall.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.CartListAdapter;
import com.example.pcmall.adapter.OrderCartListAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentOrderCreateBinding;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.AddressViewModel;
import com.example.pcmall.ui.viewmodel.CartViewModel;
import com.example.pcmall.ui.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateOrderDialog extends FullScreenDialog {
    private final String TAG = "CreateOrderDialog";
    private DialogFragmentOrderCreateBinding binding;
    private OrderViewModel orderViewModel;

    private User user;
    private OrderCartListAdapter orderCartListAdapter;
    private List<Cart> cartList;

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
        //点击地址卡片弹出选择地址窗口
        binding.addressInformationCard.setOnClickListener(v -> {
            AddressSelectDialog dialog = new AddressSelectDialog(getActivity());
            dialog.setOnDialogClosedListener(new ListenerInterface.OnDialogClosedReturnDataListener<Address>() {
                @Override
                public void onDialogClosed(DialogFragment dialogFragment, Address data) {
                    Log.d(TAG, data+"");
                }
            });
            dialog.show();
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        orderViewModel.getCartListLiveData().observe(getViewLifecycleOwner(), list -> {
            Log.d(TAG, list.size() + "");
            cartList.clear();
            cartList.addAll(list);
            orderCartListAdapter.notifyDataSetChanged();
        });

        orderViewModel.getAddressLiveData().observe(getViewLifecycleOwner(), address -> {
            binding.areaTextView.setText(address.getProvince() + address.getCity() + address.getDistrict());
            binding.addressTextView.setText(address.getAddressDetail());
            binding.receiverTextView.setText(address.getReceiverName() + " " + address.getPhone());
        });
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
