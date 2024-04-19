package com.example.pcmall.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.databinding.RecyclerviewAddressItemBinding;
import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.model.Address;

import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressItmeViewHolder> {
    private final List<Address> list;
    private ListenerInterface.OnClickListener<Address> clickListener;
    private ListenerInterface.OnLongClickListener<Address> longClickListener;

    public AddressListAdapter(List<Address> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AddressItmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewAddressItemBinding binding = RecyclerviewAddressItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AddressItmeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressItmeViewHolder holder, int position) {
        Address address = list.get(position);
        holder.binding.areaTextView.setText(address.getProvince() + address.getCity() + address.getDistrict());
        holder.binding.addressTextView.setText(address.getAddressDetail());
        holder.binding.receiverTextView.setText(address.getReceiverName() + " " + address.getPhone());
        if (address.getIsDefault() == 1) {
            holder.binding.isDefaultChip.setVisibility(View.VISIBLE);
        } else {
            holder.binding.isDefaultChip.setVisibility(View.GONE);
        }
        //点击卡片编辑地址
        if (clickListener != null) {
            holder.binding.addressCardView.setOnClickListener(v -> clickListener.onClick(v, address));
        }
        //长安卡片删除地址
        if (clickListener != null) {
            holder.binding.addressCardView.setOnLongClickListener(v -> longClickListener.onLongClick(v, address));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(ListenerInterface.OnClickListener<Address> listener) {
        this.clickListener = listener;
    }

    public void setOnLongClickListener(ListenerInterface.OnLongClickListener<Address> listener) {
        this.longClickListener = listener;
    }

    public static class AddressItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewAddressItemBinding binding;

        public AddressItmeViewHolder(RecyclerviewAddressItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
