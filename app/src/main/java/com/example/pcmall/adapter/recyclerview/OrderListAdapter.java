package com.example.pcmall.adapter.recyclerview;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.R;
import com.example.pcmall.databinding.RecyclerviewOrderItemBinding;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderItmeViewHolder> {
    private final List<Order> orderList;
    private Context context;

    private final static String[] STATUS = {"待付款", "待发货", "待收货", "交易成功", "交易取消", "退货中", "退货成功"};

    public OrderListAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderItmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewOrderItemBinding binding = RecyclerviewOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.context = parent.getContext();
        return new OrderItmeViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull OrderItmeViewHolder holder, int position) {
        Order order = orderList.get(position);
        List<Goods> goodsList = order.getGoodsList();
        holder.binding.oidTextView.setText(String.format(context.getString(R.string.recyclerview_order_item_oid), order.getOid()));
        holder.binding.statusTextView.setText(STATUS[order.getStatus()]);
        holder.binding.createdTimeTextView.setText(order.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        RecyclerView recycleView = holder.binding.recycleView;
        recycleView.setAdapter(new OrderGoodsListAdapter(goodsList));
        recycleView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewOrderItemBinding binding;

        public OrderItmeViewHolder(RecyclerviewOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
