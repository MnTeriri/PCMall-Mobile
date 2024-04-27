package com.example.pcmall.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pcmall.adapter.recyclerview.OrderListAdapter;
import com.example.pcmall.adapter.viewpager2.OrderPagerAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.ActivityOrderBinding;
import com.example.pcmall.dialog.MessageDialog;
import com.example.pcmall.dialog.OrderDetailDialog;
import com.example.pcmall.model.Order;
import com.example.pcmall.model.Pagination;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.fragment.OrderFragment;
import com.example.pcmall.ui.viewmodel.OrderViewModel;
import com.google.android.material.search.SearchView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderActivity extends AppCompatActivity {
    private final String TAG = "OrderActivity";
    private ActivityOrderBinding binding;
    private OrderViewModel orderViewModel;
    private User user;
    private OrderListAdapter orderListAdapter;
    private List<Order> orderList;
    private Pagination pagination = new Pagination();
    private String searchValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        user = ((PCMallApplication) getApplication()).getUserData();
        setContentView(binding.getRoot());

        initData();
        initView();
        initListener();
        handelObserve();

        Log.d(TAG, "OrderActivity启动");
    }

    @Override
    protected void onStart() {
        super.onStart();

        int tabId = getIntent().getIntExtra("tabId", 0);
        TabLayout orderTab = binding.orderTab;
        TabLayout.Tab tabAt = orderTab.getTabAt(tabId);
        tabAt.select();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        orderList = new ArrayList<>();
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        List<OrderFragment> fragmentList = new ArrayList<>();
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_ALL));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_PAY));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_SEND));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_DELIVER));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_REFUND));
        fragmentList.add(new OrderFragment(OrderFragment.TITLE_FINISH));

        OrderPagerAdapter orderPagerAdapter = new OrderPagerAdapter(this, fragmentList);

        //禁用预加载

        binding.viewPager.setAdapter(orderPagerAdapter);

        new TabLayoutMediator(binding.orderTab, binding.viewPager, (tab, i) -> {
            tab.setText(fragmentList.get(i).getTitle());
        }).attach();

        orderListAdapter = new OrderListAdapter(orderList);
        binding.recycleView.setAdapter(orderListAdapter);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //上拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            pagination.setCurrentPage(1);
            orderViewModel.searchOrderList(searchValue, user.getUid(), -1, pagination.getCurrentPage(), pagination.getPageSize(), true);
            orderViewModel.getRecordsFiltered(searchValue, user.getUid(), -1);
        });

        //下拉加载更多
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载RefreshLayout");
            if (pagination.nextPage()) {
                orderViewModel.searchOrderList(searchValue, user.getUid(), -1, pagination.getCurrentPage(), pagination.getPageSize(), false);
            } else {
                binding.refreshLayout.setNoMoreData(true);
            }
        });

        //搜索界面
        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            searchValue = v.getText().toString();
            pagination.setCurrentPage(1);
            orderViewModel.searchOrderList(searchValue, user.getUid(), -1, pagination.getCurrentPage(), pagination.getPageSize(), true);
            orderViewModel.getRecordsFiltered(searchValue, user.getUid(), -1);
            return true;
        });

        //SearchView隐藏
        binding.searchView.addTransitionListener((searchView, transitionState, transitionState1) -> {
            if (transitionState == SearchView.TransitionState.HIDDEN || transitionState == SearchView.TransitionState.HIDING) {
                orderList.clear();
                orderListAdapter.notifyDataSetChanged();
            }
        });

        //点击订单Card进入订单详情界面
        orderListAdapter.setOnClickListener((v, order) -> {
            OrderDetailDialog dialog = new OrderDetailDialog(this, order);
            dialog.setOnDialogClosedListener(dialogFragment -> {
                Log.d(TAG, dialogFragment.getTag() + "关闭！");
                orderViewModel.searchOrderList(searchValue, user.getUid(), -1, 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
                orderViewModel.getRecordsFiltered(searchValue, user.getUid(), -1);
            });
            dialog.show();
        });

        //点击付款按钮
        orderListAdapter.setOnPayButtonClickListener((v, order) ->
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
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
        orderListAdapter.setOnFinishButtonClickListener((v, order) ->
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
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
        orderListAdapter.setOnCancelButtonClickListener((v, order) ->
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
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
        orderListAdapter.setOnRefundButtonClickListener((v, order) ->
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
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
        Log.d(TAG, "添加ViewModel返回结果方法");
        orderViewModel.getOrderListLiveData().observe(this, list -> {
            orderList.clear();
            orderList.addAll(list);
            orderListAdapter.notifyDataSetChanged();
        });

        orderViewModel.getSearchCountLiveData().observe(this, totalCount -> {
            pagination.setTotalCount(Math.toIntExact(totalCount));
        });

        orderViewModel.getFlagLiveData().observe(this, flag -> {
            if (Objects.equals(flag, OrderViewModel.LOAD_MORE_SUCCESS)) {
                binding.refreshLayout.finishLoadMore(true);
            } else if (Objects.equals(flag, OrderViewModel.REFRESH_SUCCESS)) {
                binding.refreshLayout.finishRefresh(true);
            } else if (Objects.equals(flag, OrderViewModel.LOAD_ERROR)) {
                binding.refreshLayout.finishRefresh(false);//传入false表示刷新失败
                binding.refreshLayout.finishLoadMore(false);
            } else if (Objects.equals(flag, OrderViewModel.PAY_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("订单付款成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> {
                    orderViewModel.searchOrderList(searchValue, user.getUid(), -1, 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
                    orderViewModel.getRecordsFiltered(searchValue, user.getUid(), -1);
                });
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, OrderViewModel.FINISH_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("订单签收成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> {
                    orderViewModel.searchOrderList(searchValue, user.getUid(), -1, 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
                    orderViewModel.getRecordsFiltered(searchValue, user.getUid(), -1);
                });
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, OrderViewModel.CANCEL_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("订单取消成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> {
                    orderViewModel.searchOrderList(searchValue, user.getUid(), -1, 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
                    orderViewModel.getRecordsFiltered(searchValue, user.getUid(), -1);
                });
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, OrderViewModel.REFUND_SUCCESS)) {
                SweetAlertDialog sweetAlertDialog = new MessageDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("申请退货成功！");
                sweetAlertDialog.setOnDismissListener(dialog -> {
                    orderViewModel.searchOrderList(searchValue, user.getUid(), -1, 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
                    orderViewModel.getRecordsFiltered(searchValue, user.getUid(), -1);
                });
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, ResponseCode.ERROR.getCode())) {
                Toast.makeText(this, "错误！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        Log.d(TAG, "OrderActivity销毁");
    }
}