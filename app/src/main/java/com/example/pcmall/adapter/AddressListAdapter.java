package com.example.pcmall.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.databinding.RecyclerviewAddressItemBinding;
import com.example.pcmall.model.Address;
import com.example.pcmall.model.Cart;

import java.util.List;


public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressItmeViewHolder> {
    private final List<Address> list;

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

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AddressItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewAddressItemBinding binding;

        public AddressItmeViewHolder(RecyclerviewAddressItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
