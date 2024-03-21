package com.example.pcmall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.databinding.RecyclerviewCartItemBinding;
import com.example.pcmall.model.Cart;
import com.example.pcmall.model.Goods;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;

import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartItmeViewHolder> {
    private final List<Cart> list;
    private Context context;
    private OnItemButtonClickListener<Cart> addListener;
    private OnItemButtonClickListener<Cart> divListener;
    private OnItemButtonClickListener<Cart> deleteListener;
    private OnItemCheckBoxClickListener<Cart> selectListener;

    public CartListAdapter(List<Cart> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CartItmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewCartItemBinding binding = RecyclerviewCartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.context = parent.getContext();
        return new CartItmeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItmeViewHolder holder, int position) {
        Cart cart = list.get(position);
        Goods goods = cart.getGoods();
        GlideApp.with(context)
                .load(NetworkModule.baseUrl + "image/" + goods.getImage())
                .into(holder.binding.imageView);
        holder.binding.gnameTextView.setText(goods.getBrand().getBname() + goods.getGname());
        if (goods.getStatus() == 0 && goods.getIsDelete() == 0) {
            holder.binding.descriptionTextView.setText(goods.getDescription());
            holder.binding.priceLinearLayout.setVisibility(View.VISIBLE);
            holder.binding.selectCheckBox.setEnabled(true);
            holder.binding.selectCheckBox.setChecked(cart.getIsSelect() == 1);
            holder.binding.priceTextView.setText("￥" + goods.getPrice().toString());
            holder.binding.count.setText(cart.getCount().toString());
            if (addListener != null) {
                holder.binding.addButton.setOnClickListener(v -> addListener.onClick(v, cart));
            }
            if (divListener != null) {
                holder.binding.divBotton.setOnClickListener(v -> divListener.onClick(v, cart));
            }
            if (selectListener != null) {
                holder.binding.selectCheckBox.setOnClickListener(v -> selectListener.onCheckedChanged(v, cart));
            }
        } else {
            holder.binding.priceLinearLayout.setVisibility(View.GONE);
            holder.binding.selectCheckBox.setEnabled(false);
            holder.binding.selectCheckBox.setChecked(false);
            if (goods.getStatus() == 1) {
                holder.binding.descriptionTextView.setText("该商品缺货！");
            } else if (goods.getStatus() == 2) {
                holder.binding.descriptionTextView.setText("该商品已下架！");
            } else if (goods.getIsDelete() == 1) {
                holder.binding.descriptionTextView.setText("该商品已删除！");
            }
        }
    }

    public void setAddListener(OnItemButtonClickListener<Cart> listener) {
        this.addListener = listener;
    }

    public void setDivListener(OnItemButtonClickListener<Cart> listener) {
        this.divListener = listener;
    }

    public void setSelectListener(OnItemCheckBoxClickListener<Cart> listener) {
        this.selectListener = listener;
    }

    public void setDeleteListener(OnItemButtonClickListener<Cart> listener) {
        this.deleteListener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CartItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewCartItemBinding binding;

        public CartItmeViewHolder(RecyclerviewCartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemButtonClickListener<T> {
        void onClick(View v, T data);
    }

    public interface OnItemCheckBoxClickListener<T> {
        void onCheckedChanged(View v, T data);
    }
}