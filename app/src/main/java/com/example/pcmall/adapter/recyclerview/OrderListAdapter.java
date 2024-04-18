package com.example.pcmall.adapter.recyclerview;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.R;
import com.example.pcmall.databinding.RecyclerviewOrderItemBinding;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.model.Goods;
import com.example.pcmall.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderItmeViewHolder> {
    private final static String[] STATUS = {"待付款", "待发货", "待收货", "交易成功", "交易取消", "退货中", "退货成功"};
    private final List<Order> orderList;
    private Context context;

    private ListenerInterface.OnClickListener<Order> clickListener;
    private ListenerInterface.OnClickListener<Order> payClickListener;
    private ListenerInterface.OnClickListener<Order> cancelClickListener;
    private ListenerInterface.OnClickListener<Order> refundClickListener;

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
        holder.binding.priceTextView.setText(String.format(context.getString(R.string.price), order.getPrice()));
        Integer count = 0;
        for (Goods goods : goodsList) {
            count += goods.getCount();
        }
        holder.binding.countTextView.setText(String.format(context.getString(R.string.order_item_count), count.toString()));
        RecyclerView recycleView = holder.binding.recycleView;
        recycleView.setAdapter(new OrderGoodsListAdapter(goodsList));
        recycleView.setLayoutManager(new LinearLayoutManager(context));
        if (order.getStatus() == 0) {
            holder.binding.buttonLayout.setVisibility(View.VISIBLE);
            holder.binding.payButton.setVisibility(View.VISIBLE);
            holder.binding.cancelButton.setVisibility(View.VISIBLE);
            holder.binding.refundButton.setVisibility(View.GONE);
        } else if (order.getStatus() == 1) {
            holder.binding.buttonLayout.setVisibility(View.VISIBLE);
            holder.binding.payButton.setVisibility(View.GONE);
            holder.binding.cancelButton.setVisibility(View.VISIBLE);
            holder.binding.refundButton.setVisibility(View.GONE);
        } else if (order.getStatus() == 2 || order.getStatus() == 3) {
            holder.binding.buttonLayout.setVisibility(View.VISIBLE);
            holder.binding.payButton.setVisibility(View.GONE);
            holder.binding.cancelButton.setVisibility(View.GONE);
            holder.binding.refundButton.setVisibility(View.VISIBLE);
        } else if (order.getStatus() == 4 || order.getStatus() == 5 || order.getStatus() == 6) {
            holder.binding.buttonLayout.setVisibility(View.GONE);
        }
        if (clickListener != null) {
            holder.binding.orderCard.setOnClickListener(v -> clickListener.onClick(v, order));
        }
        if (payClickListener != null) {
            holder.binding.payButton.setOnClickListener(v -> payClickListener.onClick(v, order));
        }
        if (cancelClickListener != null) {
            holder.binding.cancelButton.setOnClickListener(v -> cancelClickListener.onClick(v, order));
        }
        if (refundClickListener != null) {
            holder.binding.refundButton.setOnClickListener(v -> refundClickListener.onClick(v, order));
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOnClickListener(ListenerInterface.OnClickListener<Order> listener) {
        this.clickListener = listener;
    }

    public void setOnPayButtonClickListener(ListenerInterface.OnClickListener<Order> listener) {
        this.payClickListener = listener;
    }

    public void setOnCancelButtonClickListener(ListenerInterface.OnClickListener<Order> listener) {
        this.cancelClickListener = listener;
    }

    public void setOnRefundButtonClickListener(ListenerInterface.OnClickListener<Order> listener) {
        this.refundClickListener = listener;
    }

    public static class OrderItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewOrderItemBinding binding;

        public OrderItmeViewHolder(RecyclerviewOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
