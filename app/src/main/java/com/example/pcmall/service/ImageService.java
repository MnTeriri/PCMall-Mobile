package com.example.pcmall.service;

import com.example.pcmall.model.response.ResponseResult;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageService {
    @Multipart
    @POST("upload")
    Observable<ResponseResult<String>> upload(@Part MultipartBody.Part file);
}
