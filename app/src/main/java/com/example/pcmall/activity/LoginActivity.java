package com.example.pcmall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.databinding.ActivityLoginBinding;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.ui.viewmodel.LoginViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        Button registerButton = binding.registerButton;
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button loginButton = binding.loginButton;
        loginButton.setOnClickListener(view -> {
            TextInputLayout uidTextInputLayout = binding.uidTextInputLayout;
            TextInputLayout passwordTextInputLayout = binding.passwordTextInputLayout;
            String uid = uidTextInputLayout.getEditText().getText().toString();
            String password = passwordTextInputLayout.getEditText().getText().toString();
            loginViewModel.login(uid, password);
        });

        loginViewModel.getLoginData().observe(this, responseResult -> {
            System.out.println("akshfjkjasf");
            Log.d(TAG, responseResult.toString());
        });


        MaterialToolbar topAppBar = binding.topAppBar;
        topAppBar.setNavigationOnClickListener(v -> finish());
    }
}
