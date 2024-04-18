package com.example.pcmall.dialog;

import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.activity.GoodsActivity;
import com.example.pcmall.adapter.recyclerview.GoodsListAdapter;
import com.example.pcmall.databinding.DialogFragmentGoodsListBinding;
import com.example.pcmall.model.Brand;
import com.example.pcmall.model.Category;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.Pagination;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.CartViewModel;
import com.example.pcmall.ui.viewmodel.GoodsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GoodsListDialog extends FullScreenDialog {
    private final String TAG = "GoodsListDialog";
    private DialogFragmentGoodsListBinding binding;
    private GoodsViewModel goodsViewModel;
    private final Category category;
    private final Brand brand;

    private List<Goods> goodsList;
    private GoodsListAdapter goodsListAdapter;
    private final Pagination pagination = new Pagination();

    public GoodsListDialog(FragmentActivity activity, Category category, Brand brand) {
        super(activity);
        this.category = category;
        this.brand = brand;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentGoodsListBinding.inflate(inflater, container, false);
        goodsViewModel = new ViewModelProvider(this).get(GoodsViewModel.class);

        initData();
        initView();
        initListener();
        handelObserve();

        goodsViewModel.searchGoodsByCidAndBid(category.getId(), brand.getId(), pagination.getCurrentPage(), pagination.getPageSize(), true);
        goodsViewModel.getRecordsFilteredByCidAndBid(category.getId(), brand.getId());

        return binding.getRoot();
    }

    private void initData() {
        Log.d(TAG, "加载数据");
        goodsList = new ArrayList<>();
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        goodsListAdapter = new GoodsListAdapter(goodsList);
        binding.recycleView.setAdapter(goodsListAdapter);
        binding.recycleView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击topAppBar的按钮返回
        binding.topAppBar.setNavigationOnClickListener(v -> dismiss());

        //上拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            pagination.setCurrentPage(1);
            goodsViewModel.searchGoodsByCidAndBid(category.getId(), brand.getId(), pagination.getCurrentPage(), pagination.getPageSize(), true);
            goodsViewModel.getRecordsFilteredByCidAndBid(category.getId(), brand.getId());
        });

        //下拉加载更多
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载RefreshLayout");
            if (pagination.nextPage()) {
                goodsViewModel.searchGoodsByCidAndBid(category.getId(), brand.getId(), pagination.getCurrentPage(), pagination.getPageSize(), false);
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

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        Log.d(TAG, "添加ViewModel返回结果方法");
        goodsViewModel.getGoodsListLiveData().observe(getViewLifecycleOwner(), list -> {
            goodsList.clear();
            goodsList.addAll(list);
            goodsListAdapter.notifyDataSetChanged();
        });

        goodsViewModel.getTotalCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            pagination.setTotalCount(Math.toIntExact(totalCount));
        });

        goodsViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, CartViewModel.LOAD_MORE_SUCCESS)) {
                binding.refreshLayout.finishLoadMore(true);
            } else if (Objects.equals(flag, CartViewModel.REFRESH_SUCCESS)) {
                binding.refreshLayout.finishRefresh(true);
            } else if (Objects.equals(flag, CartViewModel.LOAD_ERROR)) {
                binding.refreshLayout.finishRefresh(false);//传入false表示刷新失败
                binding.refreshLayout.finishLoadMore(false);
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
