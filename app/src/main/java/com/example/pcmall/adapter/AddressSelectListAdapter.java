package com.example.pcmall.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcmall.listener.ListenerInterface;
import com.example.pcmall.databinding.RecyclerviewAddressSelectItemBinding;
import com.example.pcmall.model.Address;

import java.util.List;

public class AddressSelectListAdapter extends RecyclerView.Adapter<AddressSelectListAdapter.AddressSelectItmeViewHolder> {
    private final List<Address> list;
    private int selectedItem = -1; //用来跟踪当前选中的项

    private ListenerInterface.OnClickListener<Address> editListener;
    private ListenerInterface.OnItemRadioButtonClickListener<Address> selectListener;

    public AddressSelectListAdapter(List<Address> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AddressSelectItmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewAddressSelectItemBinding binding = RecyclerviewAddressSelectItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AddressSelectItmeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressSelectItmeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Address address = list.get(position);
        holder.binding.areaTextView.setText(address.getProvince() + address.getCity() + address.getDistrict());
        holder.binding.addressTextView.setText(address.getAddressDetail());
        holder.binding.receiverTextView.setText(address.getReceiverName() + " " + address.getPhone());
        if (selectedItem == -1 && address.getIsDefault() == 1) {
            selectedItem = position;
            holder.binding.selectRadioButton.setChecked(true);
        } else {
            holder.binding.selectRadioButton.setChecked(selectedItem == position);
        }

        //选择地址RadioButton
        holder.binding.selectRadioButton.setOnClickListener(v -> {
            if (selectListener != null) {
                selectListener.onClick(v, address);
            }
            selectedItem = position;
            notifyDataSetChanged(); // 更新视图
        });

        //编辑地址Button
        if (editListener != null) {
            holder.binding.editButton.setOnClickListener(v -> editListener.onClick(v, address));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setEditListener(ListenerInterface.OnClickListener<Address> listener) {
        this.editListener = listener;
    }

    public void setSelectListener(ListenerInterface.OnItemRadioButtonClickListener<Address> listener) {
        this.selectListener = listener;
    }

    public static class AddressSelectItmeViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerviewAddressSelectItemBinding binding;

        public AddressSelectItmeViewHolder(RecyclerviewAddressSelectItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
