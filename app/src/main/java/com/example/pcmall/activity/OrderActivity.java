package com.example.pcmall.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;;

import com.example.pcmall.databinding.ActivityOrderBinding;
import com.google.android.material.tabs.TabLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderActivity extends AppCompatActivity {
    private ActivityOrderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int tabId = intent.getIntExtra("tabId", 0);

        TabLayout orderTab = binding.orderTab;
        TabLayout.Tab tabAt = orderTab.getTabAt(tabId);
        tabAt.select();
    }
}