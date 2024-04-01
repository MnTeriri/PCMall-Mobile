package com.example.pcmall.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.activity.GoodsActivity;
import com.example.pcmall.adapter.GoodsListAdapter;
import com.example.pcmall.databinding.FragmentHomeBinding;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.Pagination;
import com.example.pcmall.service.GoodsService;
import com.example.pcmall.ui.viewmodel.HomeViewModel;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private GoodsListAdapter goodsListAdapter;
    private List<Goods> goodsList;
    private Pagination pagination;
    @Inject
    public GoodsService goodsService;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initData();
        initView();
        initListener();
        handelObserve();
        Log.d(TAG, "HomeFragment启动");
        return root;
    }

    //初始化数据
    private void initData() {
        Log.d(TAG, "加载数据");
        goodsList = new ArrayList<>();
        pagination = new Pagination();
        homeViewModel.getGoodsList(pagination.getCurrentPage(), pagination.getPageSize(), true);
        homeViewModel.getTotalCount();
    }

    //初始化View
    private void initView() {
        Log.d(TAG, "初始化View");
        //初始化RecyclerView，添加Adapter和LayoutManager
        RecyclerView recycleView = binding.recycleView;
        goodsListAdapter = new GoodsListAdapter(goodsList);
        recycleView.setAdapter(goodsListAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
    }

    //添加事件
    private void initListener() {
        Log.d(TAG, "添加事件");
        RefreshLayout refreshLayout = binding.refreshLayout;
        //上拉刷新
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            pagination.setCurrentPage(1);
            homeViewModel.getGoodsList(pagination.getCurrentPage(), pagination.getPageSize(), true);
            homeViewModel.getTotalCount();
        });

        //下拉加载更多
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载RefreshLayout");
            if (pagination.nextPage()) {
                homeViewModel.getGoodsList(pagination.getCurrentPage(), pagination.getPageSize(), false);
            } else {
                binding.refreshLayout.setNoMoreData(true);
            }
        });

        //点击打开商品页面
        goodsListAdapter.setOnClickListener((v, goods) -> {
            Intent intent = new Intent(getActivity(), GoodsActivity.class);
            intent.putExtra("goods", JSON.toJSONString(goods));
            getActivity().startActivity(intent);
        });
    }

    //ViewModel返回结果
    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        homeViewModel.getGoodsLiveData().observe(getViewLifecycleOwner(), list -> {
            goodsList.clear();
            goodsList.addAll(list);
            goodsListAdapter.notifyDataSetChanged();
        });

        homeViewModel.getTotalCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            pagination.setTotalCount(Math.toIntExact(totalCount));
        });

        homeViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, HomeViewModel.LOAD_MORE_SUCCESS)) {
                binding.refreshLayout.finishLoadMore(true);
            } else if (Objects.equals(flag, HomeViewModel.REFRESH_SUCCESS)) {
                binding.refreshLayout.finishRefresh(true);
            } else if (Objects.equals(flag, HomeViewModel.LOAD_ERROR)) {
                binding.refreshLayout.finishRefresh(false);//传入false表示刷新失败
                binding.refreshLayout.finishLoadMore(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "HomeFragment销毁");
    }
}