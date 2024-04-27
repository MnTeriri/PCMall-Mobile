package com.example.pcmall.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.databinding.ActivityLoginBinding;
import com.example.pcmall.databinding.DialogCaptchaBinding;
import com.example.pcmall.dialog.MessageDialog;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.LoginRegisterViewModel;
import com.example.pcmall.utils.ImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.BaseProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    @Inject
    public SharedPreferences sharedPreferences;
    private ActivityLoginBinding binding;
    private DialogCaptchaBinding dialogBinding;
    private LoginRegisterViewModel loginRegisterViewModel;
    private AlertDialog captchaDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        dialogBinding = DialogCaptchaBinding.inflate(getLayoutInflater());
        loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);

        setContentView(binding.getRoot());

        initData();
        initView();
        initListener();
        handelObserve();

        Log.d(TAG, "LoginActivity启动");
    }

    private void initData() {
        Log.d(TAG, "加载数据");
    }

    private void initView() {
        Log.d(TAG, "初始化View");
        captchaDialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.getRoot())
                .setTitle("请输入验证码")
                .setMessage("请在下方输入框输入图片验证码")
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .setOnDismissListener(dialogInterface -> {
                    //dialog消失后复原
                    dialogBinding.progressIndicator.setVisibility(View.VISIBLE);
                    dialogBinding.captchaLinearLayout.setVisibility(View.GONE);
                    dialogBinding.captcha.setImageBitmap(null);
                }).create();//验证码弹窗
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
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
            loginRegisterViewModel.getCaptcha();//重新获取验证码图片
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

        //点击loginButton进行登录操作
        binding.loginButton.setOnClickListener(view -> {
            loginRegisterViewModel.getCaptcha();//获取验证码图片
            if (!uidValidate(uidTextInputLayout) && !passwordValidate(passwordTextInputLayout)) {
                //账号密码不违法后
                captchaDialog.show();//显示验证码弹窗
                captchaDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
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
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        //登录返回结果操作
        loginRegisterViewModel.getLoginResponse().observe(this, responseResult -> {
            //登录成功
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("data", JSON.toJSONString(responseResult.getData()));
            editor.putString("token", responseResult.getMessage());
            editor.putBoolean("remember", binding.rememberCheckBox.isChecked());
            editor.apply();
            captchaDialog.dismiss();
            SweetAlertDialog sweetAlertDialog = new MessageDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("登录成功！");
            sweetAlertDialog.setOnDismissListener(dialog -> this.finish());
            sweetAlertDialog.show();
        });

        //验证码图片获取后处理
        loginRegisterViewModel.getCaptchaResponse().observe(this, captchaString -> {
            dialogBinding.progressIndicator.setVisibility(View.GONE);
            ImageView captcha = dialogBinding.captcha;
            Bitmap bitmap = ImageUtils.decodeImageString(captchaString);
            captcha.setImageBitmap(bitmap);
            dialogBinding.captchaLinearLayout.setVisibility(View.VISIBLE);
        });

        loginRegisterViewModel.getFlagLiveData().observe(this, flag -> {
            if (Objects.equals(flag, ResponseCode.CAPTCHA_ERROR.getCode())) {
                //验证码错误
                dialogBinding.captchaTextInputLayout.setError(ResponseCode.CAPTCHA_ERROR.getMessage());
                loginRegisterViewModel.getCaptcha();
            } else if (Objects.equals(flag, ResponseCode.ACCOUNT_ERROR.getCode())) {
                //账号或密码错误
                captchaDialog.dismiss();
                new MessageDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("账号或密码错误！")
                        .show();
            }
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
