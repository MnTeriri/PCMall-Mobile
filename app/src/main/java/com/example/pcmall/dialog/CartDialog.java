package com.example.pcmall.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.pcmall.databinding.DialogFragmentCartBinding;

public class CartDialog extends FullScreenDialog {
    private final String TAG = "CartDialog";
    private DialogFragmentCartBinding binding;

    public CartDialog(FragmentActivity activity) {
        super(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
