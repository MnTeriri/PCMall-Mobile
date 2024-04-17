package com.example.pcmall.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.R;
import com.example.pcmall.databinding.RecyclerviewOrderCartItemBinding;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.Goods;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;

import java.util.List;

public class OrderCartListAdapter extends RecyclerView.Adapter<OrderCartListAdapter.OrderCartItmeViewHolder> {
    private final List<Cart> list;
    private Context context;

    public OrderCartListAdapter(List<Cart> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public OrderCartItmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewOrderCartItemBinding binding = RecyclerviewOrderCartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.context = parent.getContext();
        return new OrderCartItmeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCartItmeViewHolder holder, int position) {
        Cart cart = list.get(position);
        Goods goods = cart.getGoods();
        GlideApp.with(context)
                .load(NetworkModule.baseUrl + "image/" + goods.getImage())
                .into(holder.binding.imageView);
        holder.binding.gnameTextView.setText(String.format(context.getString(R.string.goods_item_name), goods.getBrand().getBname(), goods.getGname()));
        if (goods.getStatus() == 0 && goods.getIsDelete() == 0) {
            holder.binding.descriptionTextView.setText(goods.getDescription());
            holder.binding.priceLinearLayout.setVisibility(View.VISIBLE);
            holder.binding.priceTextView.setText(String.format(context.getString(R.string.price), goods.getPrice()));
            holder.binding.countTextView.setText(String.format(context.getString(R.string.count), cart.getCount().toString()));
        } else {
            holder.binding.priceLinearLayout.setVisibility(View.GONE);
            if (goods.getStatus() == 1) {
                holder.binding.descriptionTextView.setText("该商品缺货！");
            } else if (goods.getStatus() == 2) {
                holder.binding.descriptionTextView.setText("该商品已下架！");
            } else if (goods.getIsDelete() == 1) {
                holder.binding.descriptionTextView.setText("该商品已删除！");
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class OrderCartItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewOrderCartItemBinding binding;

        public OrderCartItmeViewHolder(RecyclerviewOrderCartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
