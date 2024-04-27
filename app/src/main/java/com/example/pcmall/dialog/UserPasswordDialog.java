package com.example.pcmall.dialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentUserPasswordBinding;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.UserViewModel;

import java.util.Objects;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserPasswordDialog extends FullScreenDialog {
    private final String TAG = "UserPasswordDialog";
    private DialogFragmentUserPasswordBinding binding;
    private UserViewModel userViewModel;
    private User user;

    @Inject
    public SharedPreferences sharedPreferences;

    public UserPasswordDialog(FragmentActivity activity) {
        super(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentUserPasswordBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        user = ((PCMallApplication) getActivity().getApplication()).getUserData();

        initData();
        initView();
        initListener();
        handelObserve();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, TAG + ".onStart()");
    }

    private void initData() {
        Log.d(TAG, "加载数据");
    }

    private void initView() {
        Log.d(TAG, "初始化View");
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击topAppBar的按钮返回
        binding.topAppBar.setNavigationOnClickListener(v -> dismiss());

        //点击修改更新密码
        binding.updateButton.setOnClickListener(v -> {
            String oldPassword = binding.oldPasswordInputText.getText().toString();
            String newPassword = binding.newPasswordInputText.getText().toString();
            String reNewPassword = binding.reNewPasswordInputText.getText().toString();
            if (oldPassword.isEmpty() || newPassword.isEmpty() || reNewPassword.isEmpty()) {
                new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("密码不能为空！").show();
                return;
            }
            if (!newPassword.equals(reNewPassword)) {
                new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("两次密码不相同！").show();
                return;
            }
            userViewModel.updatePassword(user.getUid(), oldPassword, newPassword);
        });
    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        userViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, ResponseCode.OK.getCode())) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("data", null);
                editor.putString("token", null);
                editor.putBoolean("remember", false);
                editor.apply();
                SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("修改密码成功，请重新登录！");
                sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
                sweetAlertDialog.show();
            } else if (Objects.equals(flag, ResponseCode.ACCOUNT_ERROR.getCode())) {
                new MessageDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("原密码错误！").show();
            } else if (Objects.equals(flag, ResponseCode.ERROR.getCode())) {
                Toast.makeText(getContext(), "错误！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void show() {
        super.show(TAG);
    }
}
