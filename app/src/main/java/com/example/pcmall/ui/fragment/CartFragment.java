package com.example.pcmall.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.CartListAdapter;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.FragmentCartBinding;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.Pagination;
import com.example.pcmall.model.User;
import com.example.pcmall.ui.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartFragment extends Fragment {
    private final String TAG = "CartFragment";
    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private User user;
    private CartListAdapter cartListAdapter;
    private List<Cart> cartList;
    private Pagination pagination;
    @Inject
    public SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        View root = binding.getRoot();

        initData();
        initView();
        initListener();
        handelObserve();
        Log.d(TAG, "CartFragment启动");
        return root;
    }

    //初始化数据
    private void initData() {
        Log.d(TAG, "加载数据");
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();
        cartList = new ArrayList<>();
        pagination = new Pagination();
        cartViewModel.getCartList(user.getUid(), pagination.getCurrentPage(), pagination.getPageSize(), true);
        cartViewModel.getTotalCount(user.getUid());
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
        //增加购物车商品数量
        cartListAdapter.setAddListener((v, data) -> {
            Log.d(TAG, v.toString());
            Log.d(TAG, data.toString());
        });
        //减少购物车商品数量
        cartListAdapter.setDivListener((v, data) -> {
            Log.d(TAG, v.toString());
            Log.d(TAG, data.toString());
        });
        //购物车商品选中
        cartListAdapter.setSelectListener((v, data) -> {
            Log.d(TAG, v.toString());
            Log.d(TAG, data.toString());
        });
    }

    //ViewModel返回结果
    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        cartViewModel.getCartLiveData().observe(getViewLifecycleOwner(), list -> {
            cartList.clear();
            cartList.addAll(list);
            cartListAdapter.notifyDataSetChanged();
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
            } else if (Objects.equals(flag, CartViewModel.ERROR)) {
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
