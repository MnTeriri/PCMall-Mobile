package com.example.pcmall.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.databinding.FragmentCategoryBinding;
import com.example.pcmall.ui.viewmodel.CategoryViewModel;


import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryFragment extends Fragment {
    private final String TAG = "CategoryFragment";
    private FragmentCategoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CategoryViewModel categoryViewModel =
                new ViewModelProvider(this).get(CategoryViewModel.class);

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "CategoryFragment销毁");
    }
}