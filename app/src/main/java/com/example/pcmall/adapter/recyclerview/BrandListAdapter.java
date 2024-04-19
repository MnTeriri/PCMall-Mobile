package com.example.pcmall.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.databinding.RecyclerviewBrandItemBinding;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.model.Brand;
import com.example.pcmall.module.GlideApp;
import com.example.pcmall.module.NetworkModule;

import java.util.List;

public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.BrandItmeViewHolder> {
    private final List<Brand> list;
    private Context context;

    private ListenerInterface.OnClickListener<Brand> clickListener;

    public BrandListAdapter(List<Brand> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BrandItmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewBrandItemBinding binding = RecyclerviewBrandItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.context = parent.getContext();
        return new BrandItmeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandItmeViewHolder holder, int position) {
        Brand brand = list.get(position);
        GlideApp.with(context)
                .load(NetworkModule.baseUrl + "image/" + brand.getImage())
                .into(holder.binding.imageView);
        holder.binding.bnameTextView.setText(brand.getBname());
        //点击品牌事件
        if (clickListener != null) {
            holder.binding.brandLayout.setOnClickListener(v -> clickListener.onClick(v, brand));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(ListenerInterface.OnClickListener<Brand> listener) {
        this.clickListener = listener;
    }

    public static class BrandItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewBrandItemBinding binding;

        public BrandItmeViewHolder(RecyclerviewBrandItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
