package com.example.pcmall.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.R;
import com.example.pcmall.activity.GoodsActivity;
import com.example.pcmall.adapter.CartListAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.FragmentCartBinding;
import com.example.pcmall.dialog.CreateOrderDialog;
import com.example.pcmall.dialog.MessageDialog;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.Pagination;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.CartViewModel;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartFragment extends Fragment {
    private final String TAG = "CartFragment";
    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private User user;
    private CartListAdapter cartListAdapter;
    private List<Cart> cartList;
    private Pagination pagination = new Pagination();
    @Inject
    public SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        View root = binding.getRoot();
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        if (user != null) {
            initData();
            initView();
            initListener();
            handelObserve();

            cartViewModel.getCartList(user.getUid(), pagination.getCurrentPage(), pagination.getPageSize(), true);
            cartViewModel.getTotalCount(user.getUid());
        } else {
            binding.information.setVisibility(View.VISIBLE);
            binding.refreshLayout.setVisibility(View.GONE);
            binding.selectLayout.setVisibility(View.GONE);
        }

        Log.d(TAG, "CartFragment启动");
        return root;
    }

    //初始化数据
    private void initData() {
        Log.d(TAG, "加载数据");
        cartList = new ArrayList<>();
    }

    //初始化View
    private void initView() {
        Log.d(TAG, "初始化View");
        RecyclerView recycleView = binding.recycleView;
        cartListAdapter = new CartListAdapter(cartList);
        recycleView.setAdapter(cartListAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    //添加事件
    private void initListener() {
        Log.d(TAG, "添加事件");
        //上拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            pagination.setCurrentPage(1);
            cartViewModel.getCartList(user.getUid(), pagination.getCurrentPage(), pagination.getPageSize(), true);
            cartViewModel.getTotalCount(user.getUid());
        });

        //下拉加载更多
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载RefreshLayout");
            if (pagination.nextPage()) {
                cartViewModel.getCartList(user.getUid(), pagination.getCurrentPage(), pagination.getPageSize(), false);
            } else {
                binding.refreshLayout.setNoMoreData(true);
            }
        });

        //点击打开商品页面
        cartListAdapter.setOnClickListener((v, cart) -> {
            Intent intent = new Intent(getActivity(), GoodsActivity.class);
            intent.putExtra("goods", JSON.toJSONString(cart.getGoods()));
            getActivity().startActivity(intent);
        });

        //长按进行购物车删除
        cartListAdapter.setOnLongClickListener((v, cart) -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setMessage("确认要将这件商品删除？")
                    .setNeutralButton("取消", null)
                    .setPositiveButton(
                            "确认",
                            (dialog, which) -> cartViewModel.deleteCart(cart.getId())
                    )
                    .show();
            return true;
        });

        //增加购物车商品数量
        cartListAdapter.setAddListener((v, data) -> {
            cartViewModel.addCartCount(data.getId());
        });

        //减少购物车商品数量
        cartListAdapter.setDivListener((v, data) -> {
            cartViewModel.subCartCount(data.getId());
        });

        //购物车商品选中
        cartListAdapter.setSelectListener((v, data) -> {
            MaterialCheckBox checkBox = (MaterialCheckBox) v;
            cartViewModel.selectCart(data.getId(), checkBox.getCheckedState());
        });

        //购物车全选和全不选
        binding.selectAllCheckBox.setOnClickListener(v -> {
            MaterialCheckBox checkBox = (MaterialCheckBox) binding.selectAllCheckBox;
            cartViewModel.selectAllCart(user.getUid(), checkBox.getCheckedState());
        });

        //结算按钮
        binding.createOrderButton.setOnClickListener(v -> {
            if (cartViewModel.getSelectItemCountLiveData().getValue() > 0) {
                CreateOrderDialog dialog = new CreateOrderDialog(getActivity());
                dialog.setOnDialogClosedListener(dialogFragment -> {
                    pagination.setCurrentPage(1);
                    cartViewModel.getCartList(user.getUid(), pagination.getCurrentPage(), pagination.getPageSize(), true);
                    cartViewModel.getTotalCount(user.getUid());
                });
                dialog.show();
            } else {
                new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("购物车为空！").show();
            }
        });
    }

    //ViewModel返回结果
    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        cartViewModel.getCartListLiveData().observe(getViewLifecycleOwner(), list -> {
            cartList.clear();
            cartList.addAll(list);
            cartListAdapter.notifyDataSetChanged();
        });

        cartViewModel.getSelectItemTotalPriceLiveData().observe(getViewLifecycleOwner(), bigDecimal -> {
            binding.totalPriceTextView.setText(String.format(getString(R.string.price), bigDecimal.toString()));
        });

        cartViewModel.getTotalCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            pagination.setTotalCount(Math.toIntExact(totalCount));
        });

        cartViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, CartViewModel.LOAD_MORE_SUCCESS)) {
                binding.refreshLayout.finishLoadMore(true);
            } else if (Objects.equals(flag, CartViewModel.REFRESH_SUCCESS)) {
                binding.refreshLayout.finishRefresh(true);
            } else if (Objects.equals(flag, CartViewModel.LOAD_ERROR)) {
                binding.refreshLayout.finishRefresh(false);//传入false表示刷新失败
                binding.refreshLayout.finishLoadMore(false);
            } else if (Objects.equals(flag, ResponseCode.OK.getCode())) {
                cartViewModel.getCartList(user.getUid(), 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
            } else if (Objects.equals(flag, ResponseCode.GOODS_NOT_ENOUGH_ERROR.getCode())) {
                Toast.makeText(getContext(), "商品库存不足！", Toast.LENGTH_SHORT).show();
                cartViewModel.getCartList(user.getUid(), 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
            } else if (Objects.equals(flag, ResponseCode.CART_MIN_COUNT_ERROR.getCode())) {
                Toast.makeText(getContext(), "购物车数量最小！", Toast.LENGTH_SHORT).show();
                cartViewModel.getCartList(user.getUid(), 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
            } else if (Objects.equals(flag, ResponseCode.CART_GOODS_ERROR.getCode())) {
                cartViewModel.getCartList(user.getUid(), 1, pagination.getCurrentPage() * pagination.getPageSize(), true);
            } else if (Objects.equals(flag, ResponseCode.ERROR.getCode())) {
                Toast.makeText(getContext(), "错误！", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
