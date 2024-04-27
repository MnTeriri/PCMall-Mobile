package com.example.pcmall.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson2.JSON;
import com.bumptech.glide.Glide;
import com.example.pcmall.application.PCMallApplication;
import com.example.pcmall.databinding.DialogFragmentUserInformationBinding;
import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.ui.viewmodel.UserViewModel;
import com.example.pcmall.utils.ImageUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserInformationDialog extends FullScreenDialog {
    private final String TAG = "UserInformationDialog";
    private DialogFragmentUserInformationBinding binding;
    private UserViewModel userViewModel;
    private User user;

    @Inject
    public SharedPreferences sharedPreferences;

    public UserInformationDialog(FragmentActivity activity) {
        super(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogFragmentUserInformationBinding.inflate(inflater, container, false);
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
        binding.unameInputText.setText(user.getUname());
    }

    private void initListener() {
        Log.d(TAG, "添加事件");
        //点击topAppBar的按钮返回
        binding.topAppBar.setNavigationOnClickListener(v -> dismiss());

        //点击按钮选择照片
        binding.imageButton.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openSystemGallery(SelectMimeType.ofImage())
                    .setCropEngine((fragment, srcUri, destinationUri, dataSource, requestCode) -> {
                        UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
                        uCrop.setImageEngine(new UCropImageEngine() {
                            @Override
                            public void loadImage(Context context, String url, ImageView imageView) {
                                Glide.with(context).load(url).into(imageView);
                            }

                            @Override
                            public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {

                            }
                        });
                        uCrop.withAspectRatio(1, 1);
                        uCrop.start(fragment.getActivity(), fragment, requestCode);
                    })
                    .forSystemResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(ArrayList<LocalMedia> result) {
                            LocalMedia localMedia = result.get(0);
                            Log.d(TAG, "剪切地址：" + localMedia.getCutPath());
                            Log.d(TAG, "原地址：" + localMedia.getRealPath());
                            File file = new File(localMedia.getCutPath());
                            Bitmap bitmap = ImageUtils.getImageFromFile(file);
                            binding.userImage.setImageBitmap(bitmap);
                            userViewModel.uploadImage(file);
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getContext(), "取消选择照片！", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        //上传按钮
        binding.updateButton.setOnClickListener(v -> {
            String uname = binding.unameInputText.getText().toString();
            String image = userViewModel.getImage();

            userViewModel.updateInformation(new User()
                    .setUid(user.getUid())
                    .setUname(uname)
                    .setImage(image)
            );
        });

    }

    private void handelObserve() {
        Log.d(TAG, "添加ViewModel返回结果方法");
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("data", JSON.toJSONString(user));
            editor.apply();
            SweetAlertDialog sweetAlertDialog = new MessageDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("修改信息成功！");
            sweetAlertDialog.setOnDismissListener(dialog -> dismiss());
            sweetAlertDialog.show();
        });

        userViewModel.getFlagLiveData().observe(getViewLifecycleOwner(), flag -> {
            if (Objects.equals(flag, ResponseCode.OK.getCode())) {
                Toast.makeText(getContext(), "照片上传成功！", Toast.LENGTH_SHORT).show();
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
