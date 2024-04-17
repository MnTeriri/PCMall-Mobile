package com.example.pcmall.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.recyclerview.OrderListAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.FragmentOrderBinding;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.model.Order;
import com.example.pcmall.model.Pagination;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderFragment extends Fragment {
    private final String TAG;
    public final static Integer TITLE_ALL = -1;
    public final static Integer TITLE_PAY = 0;
    public final static Integer TITLE_SEND = 1;
    public final static Integer TITLE_DELIVER = 2;
    public final static Integer TITLE_REFUND = 3;
    public final static Integer TITLE_COMMENT = 4;
    private final static Map<Integer, String> TITLE;

    static {
        TITLE = new HashMap<>();
        TITLE.put(TITLE_ALL, "全部");
        TITLE.put(TITLE_PAY, "待付款");
        TITLE.put(TITLE_SEND, "待发货");
        TITLE.put(TITLE_DELIVER, "待收货");
        TITLE.put(TITLE_REFUND, "退款/售后");
        TITLE.put(TITLE_COMMENT, "待评价");
    }

    private final Integer type;
    private FragmentOrderBinding binding;
    private OrderViewModel orderViewModel;
    private User user;
    private OrderListAdapter orderListAdapter;
    private List<Order> orderList;
    private Pagination pagination = new Pagination();

    public OrderFragment(Integer type) {
        this.type = type;
        this.TAG = "OrderFragment_" + getTitle();
    }

    public String getTitle() {
        return TITLE.get(type);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        initData();
        initView();
        initListener();
        handelObserve();

        orderViewModel.searchOrderList("", user.getUid(), type, pagination.getCurrentPage(), pagination.getPageSize(), true);
        orderViewModel.getRecordsFiltered("", user.getUid(), type);

        Log.d(TAG, "OrderFragment启动");

        return binding.getRoot();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        orderList = new ArrayList<>();
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        orderListAdapter = new OrderListAdapter(orderList);
        binding.recycleView.setAdapter(orderListAdapter);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //上拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            pagination.setCurrentPage(1);
            orderViewModel.searchOrderList("", user.getUid(), type, pagination.getCurrentPage(), pagination.getPageSize(), true);
            orderViewModel.getRecordsFiltered("", user.getUid(), type);
        });

        //下拉加载更多
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载RefreshLayout");
            if (pagination.nextPage()) {
                orderViewModel.searchOrderList("", user.getUid(), type, pagination.getCurrentPage(), pagination.getPageSize(), false);
            } else {
                binding.refreshLayout.setNoMoreData(true);
            }
        });

        //点击订单Card进入订单详情界面
        orderListAdapter.setOnClickListener(new ListenerInterface.OnClickListener<Order>() {
            @Override
            public void onClick(View v, Order data) {
                Log.d(TAG, data.toString());
            }
        });

        //点击付款按钮
        orderListAdapter.setOnPayButtonClickListener(new ListenerInterface.OnClickListener<Order>() {
            @Override
            public void onClick(View v, Order data) {
                Log.d(TAG, data.toString());
            }
        });

        //点击取消订单按钮
        orderListAdapter.setOnCancelButtonClickListener(new ListenerInterface.OnClickListener<Order>() {
            @Override
            public void onClick(View v, Order data) {
                Log.d(TAG, data.toString());
            }
        });

        //点击退货按钮
        orderListAdapter.setOnRefundButtonClickListener(new ListenerInterface.OnClickListener<Order>() {
            @Override
            public void onClick(View v, Order data) {
                Log.d(TAG, data.toString());
            }
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        orderViewModel.getOrderListLiveData().observe(getViewLifecycleOwner(), list -> {
            orderList.clear();
            orderList.addAll(list);
            orderListAdapter.notifyDataSetChanged();
        });

        orderViewModel.getSearchCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            pagination.setTotalCount(Math.toIntExact(totalCount));
        });

        orderViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, OrderViewModel.LOAD_MORE_SUCCESS)) {
                binding.refreshLayout.finishLoadMore(true);
            } else if (Objects.equals(flag, OrderViewModel.REFRESH_SUCCESS)) {
                binding.refreshLayout.finishRefresh(true);
            } else if (Objects.equals(flag, OrderViewModel.LOAD_ERROR)) {
                binding.refreshLayout.finishRefresh(false);//传入false表示刷新失败
                binding.refreshLayout.finishLoadMore(false);
            } else if (Objects.equals(flag, ResponseCode.ERROR.getCode())) {
                Toast.makeText(getContext(), "错误！", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
