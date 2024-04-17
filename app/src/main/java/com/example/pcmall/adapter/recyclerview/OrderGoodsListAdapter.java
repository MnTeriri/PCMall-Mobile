package com.example.pcmall.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.R;
import com.example.pcmall.databinding.RecyclerviewOrderGoodsItemBinding;
import com.example.pcmall.model.Goods;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;

import java.util.List;

public class OrderGoodsListAdapter extends RecyclerView.Adapter<OrderGoodsListAdapter.OrderGoodsViewHolder> {
    private final List<Goods> goodsList;
    private Context context;

    public OrderGoodsListAdapter(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    @NonNull
    @Override
    public OrderGoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewOrderGoodsItemBinding binding = RecyclerviewOrderGoodsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.context = parent.getContext();
        return new OrderGoodsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderGoodsViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        GlideApp.with(context)
                .load(NetworkModule.baseUrl + "image/" + goods.getImage())
                .into(holder.binding.imageView);
        holder.binding.gnameTextView.setText(String.format(context.getString(R.string.goods_item_name), goods.getBrand().getBname(), goods.getGname()));
        holder.binding.descriptionTextView.setText(goods.getDescription());
        holder.binding.priceLinearLayout.setVisibility(View.VISIBLE);
        holder.binding.priceTextView.setText(String.format(context.getString(R.string.price), goods.getPrice()));
        holder.binding.countTextView.setText(String.format(context.getString(R.string.count), goods.getCount().toString()));
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    public static class OrderGoodsViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewOrderGoodsItemBinding binding;

        public OrderGoodsViewHolder(RecyclerviewOrderGoodsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
