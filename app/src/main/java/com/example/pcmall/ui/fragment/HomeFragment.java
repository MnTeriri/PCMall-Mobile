package com.example.pcmall.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.adapter.GoodsListAdapter;
import com.example.pcmall.databinding.FragmentHomeBinding;
import com.example.pcmall.model.Goods;
import com.example.pcmall.service.GoodsService;
import com.example.pcmall.ui.viewmodel.GoodsViewModel;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private GoodsViewModel goodsViewModel;
    private GoodsListAdapter goodsListAdapter;
    private List<Goods> goodsList;
    private boolean reFresh;
    @Inject
    public GoodsService goodsService;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        goodsViewModel = new ViewModelProvider(this).get(GoodsViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();
        initListener();
        handelObserve();
        Log.d(TAG, "HomeFragment启动");
        return root;
    }

    //初始化View
    private void initView() {
        Log.d(TAG, "加载数据");
        reFresh = false;
        goodsList = new ArrayList<>();

        Log.d(TAG, "初始化View");
        //初始化RecyclerView，添加Adapter和LayoutManager
        RecyclerView recycleView = binding.recycleView;
        goodsListAdapter = new GoodsListAdapter(goodsList);
        recycleView.setAdapter(goodsListAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));

        //初始化RefreshLayout
        RefreshLayout refreshLayout = binding.refreshLayout;
        refreshLayout.setRefreshHeader(new ClassicsHeader(this.getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this.getContext()));
    }

    //添加事件
    private void initListener() {
        Log.d(TAG, "添加事件");
        RefreshLayout refreshLayout = binding.refreshLayout;
        //上拉刷新
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            Log.d(TAG, "上拉刷新RefreshLayout");
            //refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            reFresh=true;
            goodsViewModel.getGoodsList(1, 20, true);
        });
        //下拉加载更多
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            Log.d(TAG, "下拉加载RefreshLayout");
            //refreshlayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
            reFresh=false;
            goodsViewModel.getGoodsList(1, 20, false);
        });
    }

    //ViewModel返回结果
    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        goodsViewModel.getGoodsLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list == null) {
                if (reFresh) {
                    binding.refreshLayout.finishRefresh(false);//传入false表示刷新失败
                } else {
                    binding.refreshLayout.finishLoadMore(false);//传入false表示刷新失败
                }
                return;
            }
            if (reFresh) {
                binding.refreshLayout.finishRefresh(true);//传入false表示刷新失败
            } else {
                binding.refreshLayout.finishLoadMore(true);//传入false表示刷新失败
            }
            goodsList.clear();
            goodsList.addAll(list);
            goodsListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "HomeFragment销毁");
    }
}