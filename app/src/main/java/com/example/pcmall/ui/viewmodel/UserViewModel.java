package com.example.pcmall.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseCode;
import com.example.pcmall.model.response.ResponseResult;
import com.example.pcmall.service.ImageService;
import com.example.pcmall.service.UserService;
import com.example.pcmall.utils.RetrofitUtils;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private final String TAG = "UserViewModel";
    private final ImageService imageService;
    private final UserService userService;
    private final CompositeDisposable compositeDisposable;

    @Getter
    private final MutableLiveData<User> userLiveData;
    @Getter
    private final MutableLiveData<Integer> flagLiveData;
    @Getter
    private String image;

    @Inject
    public UserViewModel(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
        this.compositeDisposable = new CompositeDisposable();
        this.userLiveData = new MutableLiveData<>();
        this.flagLiveData = new MutableLiveData<>();
    }

    public void uploadImage(File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        Disposable disposable = imageService.upload(filePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> {
                            Log.d(TAG, responseResult.toString());
                            image = responseResult.getData();
                            flagLiveData.setValue(ResponseCode.OK.getCode());
                        },
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void updateInformation(User user) {
        Disposable disposable = userService.updateInformation(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> {
                            Log.d(TAG, responseResult.toString());
                            userLiveData.setValue(responseResult.getData());
                        },
                        throwable -> flagLiveData.setValue(ResponseCode.ERROR.getCode())
                );
        compositeDisposable.add(disposable);
    }

    public void updatePassword(String uid, String oldPassword, String newPassword) {
        Disposable disposable = userService.updatePassword(uid, oldPassword, newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseResult -> flagLiveData.setValue(ResponseCode.OK.getCode()),
                        throwable -> {
                            ResponseResult<String> message = RetrofitUtils.getErrorMessage(throwable);
                            if (message != null) {
                                flagLiveData.setValue(message.getCode());
                            }
                        }
                );
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        Log.d(TAG, "销毁UserViewModel，清除compositeDisposable");
    }
}
