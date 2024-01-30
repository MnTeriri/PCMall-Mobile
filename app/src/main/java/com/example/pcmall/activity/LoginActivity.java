package com.example.pcmall.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.databinding.ActivityLoginBinding;
import com.example.pcmall.databinding.DialogCaptchaBinding;
import com.example.pcmall.model.response.ResponseStatus;
import com.example.pcmall.ui.viewmodel.LoginRegisterViewModel;
import com.example.pcmall.utils.ImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private DialogCaptchaBinding dialogBinding;
    private LoginRegisterViewModel loginRegisterViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialogBinding = DialogCaptchaBinding.inflate(getLayoutInflater());
        loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);

        //初始化一些简单的操作
        //点击registerButton启动RegisterActivity
        binding.registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        //点击topAppBar的按钮返回到MainActivity
        binding.topAppBar.setNavigationOnClickListener(v -> finish());

        //点击captchaButton刷新验证码图片
        dialogBinding.captchaButton.setOnClickListener(view -> {
            //dialog复原
            dialogBinding.progressIndicator.setVisibility(View.VISIBLE);
            dialogBinding.captchaLinearLayout.setVisibility(View.GONE);
            dialogBinding.captcha.setImageBitmap(null);
            loginRegisterViewModel.captchaImageString();//重新获取验证码图片
        });

        //TextInputLayout聚焦事件
        TextInputLayout uidTextInputLayout = binding.uidTextInputLayout;
        uidTextInputLayout.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                uidValidate(uidTextInputLayout);
            }
        });
        TextInputLayout passwordTextInputLayout = binding.passwordTextInputLayout;
        passwordTextInputLayout.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                passwordValidate(passwordTextInputLayout);
            }
        });

        login();
        Log.d(TAG, "LoginActivity启动");
    }

    //登录操作
    private void login() {
        Button loginButton = binding.loginButton;
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.getRoot())
                .setTitle("请输入验证码")
                .setMessage("请在下方输入框输入图片验证码")
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", (dialogInterface, i) -> {
                    Toast.makeText(LoginActivity.this, "登录取消！", Toast.LENGTH_SHORT).show();
                })
                .setOnDismissListener(dialogInterface -> {
                    //dialog消失后复原
                    dialogBinding.progressIndicator.setVisibility(View.VISIBLE);
                    dialogBinding.captchaLinearLayout.setVisibility(View.GONE);
                    dialogBinding.captcha.setImageBitmap(null);
                }).create();//验证码弹窗


        //点击loginButton进行登录操作
        loginButton.setOnClickListener(view -> {
            loginRegisterViewModel.captchaImageString();//获取验证码图片
            TextInputLayout uidTextInputLayout = binding.uidTextInputLayout;
            TextInputLayout passwordTextInputLayout = binding.passwordTextInputLayout;
            if (!uidValidate(uidTextInputLayout) && !passwordValidate(passwordTextInputLayout)) {
                //账号密码不违法后
                dialog.show();//显示验证码弹窗
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                    TextInputLayout captchaTextInputLayout = dialogBinding.captchaTextInputLayout;
                    if (!captchaValidate(captchaTextInputLayout)) {
                        //验证码不违法后，登录
                        String uid = uidTextInputLayout.getEditText().getText().toString();
                        String password = passwordTextInputLayout.getEditText().getText().toString();
                        String code = captchaTextInputLayout.getEditText().getText().toString();
                        loginRegisterViewModel.login(uid, password, code);
                    }
                });
            }
        });

        //登录返回结果操作
        loginRegisterViewModel.getLoginResponse().observe(this, responseResult -> {
            Toast.makeText(this, responseResult.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, responseResult.toString());
            if (Objects.equals(responseResult.getCode(), ResponseStatus.CAPTCHA_ERROR.getCode())) {
                dialogBinding.captchaTextInputLayout.setError(ResponseStatus.CAPTCHA_ERROR.getMessage());
            }
        });

        //验证码图片获取后处理
        loginRegisterViewModel.getCaptchaImageResponse().observe(this, responseResult -> {
            dialogBinding.progressIndicator.setVisibility(View.GONE);
            ImageView captcha = dialogBinding.captcha;
            Bitmap bitmap = ImageUtils.decodeImageString(responseResult.getData());
            captcha.setImageBitmap(bitmap);
            dialogBinding.captchaLinearLayout.setVisibility(View.VISIBLE);
        });
    }

    private boolean uidValidate(TextInputLayout uidTextInputLayout) {
        EditText editText = uidTextInputLayout.getEditText();
        String uid = editText.getText().toString();
        if (uid.isEmpty()) {
            uidTextInputLayout.setError("UID不得为空！");
            return true;
        }
        if (uid.length() != 9) {
            uidTextInputLayout.setError("UID必须是9位数字！");
            return true;
        }
        uidTextInputLayout.setError(null);
        return false;
    }

    private boolean passwordValidate(TextInputLayout passwordTextInputLayout) {
        EditText editText = passwordTextInputLayout.getEditText();
        String password = editText.getText().toString();
        if (password.isEmpty()) {
            passwordTextInputLayout.setError("密码不得为空！");
            return true;
        }
        if (password.length() < 5 || password.length() > 16) {
            passwordTextInputLayout.setError("密码必须是5到16位字符！");
            return true;
        }
        passwordTextInputLayout.setError(null);
        return false;
    }

    private boolean captchaValidate(TextInputLayout captchaTextInputLayout) {
        EditText editText = captchaTextInputLayout.getEditText();
        String code = editText.getText().toString();
        if (code.isEmpty()) {
            captchaTextInputLayout.setError("验证码不得为空！");
            return true;
        }
        if (code.length() != 5) {
            captchaTextInputLayout.setError("验证码必须是5位字符！");
            return true;
        }
        captchaTextInputLayout.setError(null);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        dialogBinding = null;
        loginRegisterViewModel = null;
        Log.d(TAG, "LoginActivity销毁");
    }
}
