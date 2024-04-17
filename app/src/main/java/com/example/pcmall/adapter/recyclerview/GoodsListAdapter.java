package com.example.pcmall.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.R;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.databinding.RecyclerviewGoodsItemBinding;
import com.example.pcmall.model.Goods;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;

import java.util.List;

public class GoodsListAdapter extends RecyclerView.Adapter<GoodsListAdapter.GoodsItmeViewHolder> {
    private final List<Goods> list;
    private Context context;
    private ListenerInterface.OnClickListener<Goods> clickListener;

    public GoodsListAdapter(List<Goods> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public GoodsItmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewGoodsItemBinding inflate = RecyclerviewGoodsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.context = parent.getContext();
        return new GoodsItmeViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsItmeViewHolder holder, int position) {
        Goods goods = list.get(position);
        GlideApp.with(context)
                .load(NetworkModule.baseUrl + "image/" + goods.getImage())
                .into(holder.binding.imageView);
        holder.binding.gnameTextView.setText(String.format(context.getString(R.string.goods_item_name), goods.getBrand().getBname(), goods.getGname()));
        holder.binding.descriptionTextView.setText(goods.getDescription());
        holder.binding.priceTextView.setText(String.format(context.getString(R.string.price), goods.getPrice()));
        //点击事件，进入商品界面
        if (clickListener != null) {
            holder.binding.getRoot().setOnClickListener(v -> clickListener.onClick(v, goods));
        }
    }

    public void setOnClickListener(ListenerInterface.OnClickListener<Goods> listener) {
        this.clickListener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GoodsItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewGoodsItemBinding binding;

        public GoodsItmeViewHolder(@NonNull RecyclerviewGoodsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
