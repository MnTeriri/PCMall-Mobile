package com.example.pcmall.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.activity.GoodsActivity;
import com.example.pcmall.adapter.recyclerview.GoodsListAdapter;
import com.example.pcmall.databinding.FragmentHomeBinding;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.Pagination;
import com.example.pcmall.ui.viewmodel.HomeViewModel;
import com.google.android.material.search.SearchView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private GoodsListAdapter goodsListAdapter;
    private GoodsListAdapter searchListAdapter;
    private List<Goods> goodsList;
    private List<Goods> searchList;
    private String searchValue = "";
    private final Pagination searchPagination = new Pagination();
    private final Pagination pagination = new Pagination();
    private FragmentActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        activity = getActivity();

        initData();
        initView();
        initListener();
        handelObserve();

        Log.d(TAG, "HomeFragment启动");
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, TAG + ".onStart()");
        Log.d(TAG, "网络请求数据。。。");
        homeViewModel.getGoodsList(pagination.getCurrentPage(), pagination.getPageSize(), true);
        homeViewModel.getTotalCount();
    }

    //初始化数据
    private void initData() {
        Log.d(TAG, "加载数据");
        goodsList = new ArrayList<>();
        searchList = new ArrayList<>();
    }

    //初始化View
    private void initView() {
        Log.d(TAG, "初始化View");
        //初始化RecyclerView，添加Adapter和LayoutManager
        RecyclerView recycleView = binding.recycleView;
        RecyclerView searchRecycleView = binding.searchRecycleView;
        goodsListAdapter = new GoodsListAdapter(goodsList);
        searchListAdapter = new GoodsListAdapter(searchList);
        recycleView.setAdapter(goodsListAdapter);
        searchRecycleView.setAdapter(searchListAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        searchRecycleView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
    }

    //添加事件
    private void initListener() {
        Log.d(TAG, "添加事件");
        //主页上拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            pagination.setCurrentPage(1);
            homeViewModel.getGoodsList(pagination.getCurrentPage(), pagination.getPageSize(), true);
            homeViewModel.getTotalCount();
        });

        //主页下拉加载更多
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载RefreshLayout");
            if (pagination.nextPage()) {
                homeViewModel.getGoodsList(pagination.getCurrentPage(), pagination.getPageSize(), false);
            } else {
                binding.refreshLayout.setNoMoreData(true);
            }
        });

        //点击打开商品页面
        goodsListAdapter.setOnClickListener((v, goods) -> {
            Intent intent = new Intent(activity, GoodsActivity.class);
            intent.putExtra("goods", JSON.toJSONString(goods));
            activity.startActivity(intent);
        });

        //搜索界面
        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            searchValue = v.getText().toString();
            searchPagination.setCurrentPage(1);
            homeViewModel.getSearchList(searchValue, searchPagination.getCurrentPage(), searchPagination.getPageSize(), true);
            homeViewModel.getRecordsFiltered(searchValue);
            return true;
        });

        //SearchView隐藏
        binding.searchView.addTransitionListener((searchView, transitionState, transitionState1) -> {
            if (transitionState == SearchView.TransitionState.HIDDEN || transitionState == SearchView.TransitionState.HIDING) {
                searchList.clear();
                searchListAdapter.notifyDataSetChanged();
            }
        });

        RefreshLayout searchRefreshLayout = binding.searchRefreshLayout;
        //SearchView上拉刷新
        searchRefreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新SearchRefreshLayout");
            searchPagination.setCurrentPage(1);
            homeViewModel.getSearchList(searchValue, searchPagination.getCurrentPage(), searchPagination.getPageSize(), true);
            homeViewModel.getRecordsFiltered(searchValue);
        });

        //SearchView下拉加载更多
        searchRefreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载SearchRefreshLayout");
            if (searchPagination.nextPage()) {
                homeViewModel.getSearchList(searchValue, searchPagination.getCurrentPage(), searchPagination.getPageSize(), false);
            } else {
                binding.searchRefreshLayout.setNoMoreData(true);
            }
        });

        //点击打开商品页面
        searchListAdapter.setOnClickListener((v, goods) -> {
            Intent intent = new Intent(activity, GoodsActivity.class);
            intent.putExtra("goods", JSON.toJSONString(goods));
            activity.startActivity(intent);
        });

    }

    //ViewModel返回结果
    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        homeViewModel.getGoodsLiveData().observe(getViewLifecycleOwner(), list -> {
            goodsList.clear();
            goodsList.addAll(list);
            goodsListAdapter.notifyDataSetChanged();
            new Thread(() -> {
                try {
                    Thread.sleep(750);
                    activity.runOnUiThread(() -> {
                        binding.progressIndicator.hide();
                        binding.recycleView.setVisibility(View.VISIBLE);
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        homeViewModel.getSearchLiveData().observe(getViewLifecycleOwner(), list -> {
            searchList.clear();
            searchList.addAll(list);
            searchListAdapter.notifyDataSetChanged();
        });

        homeViewModel.getTotalCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            pagination.setTotalCount(Math.toIntExact(totalCount));
        });

        homeViewModel.getSearchCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            searchPagination.setTotalCount(Math.toIntExact(totalCount));
        });

        homeViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, HomeViewModel.LOAD_MORE_SUCCESS)) {
                binding.refreshLayout.finishLoadMore(true);
                binding.searchRefreshLayout.finishLoadMore(true);
            } else if (Objects.equals(flag, HomeViewModel.REFRESH_SUCCESS)) {
                binding.refreshLayout.finishRefresh(true);
                binding.searchRefreshLayout.finishRefresh(true);
            } else if (Objects.equals(flag, HomeViewModel.LOAD_ERROR)) {
                binding.refreshLayout.finishRefresh(false);//传入false表示刷新失败
                binding.refreshLayout.finishLoadMore(false);
                binding.searchRefreshLayout.finishRefresh(false);
                binding.searchRefreshLayout.finishLoadMore(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HomeFragment销毁");
    }
}