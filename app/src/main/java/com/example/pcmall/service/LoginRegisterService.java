package com.example.pcmall.service;

import com.example.pcmall.model.LoginUser;
import com.example.pcmall.model.response.ResponseResult;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginRegisterService {
    @FormUrlEncoded
    @POST("login")
    Observable<ResponseResult<LoginUser>> login(
            @Field("uid") String uid,
            @Field("password") String password,
            @Field("code") String code);

    @FormUrlEncoded
    @POST("register")
    Observable<ResponseResult<String>> register(@Field("uid") String uid,
                                                @Field("password") String password,
                                                @Field("code") String code);

    @GET("captcha.jpg")
    Observable<ResponseResult<String>> getCaptcha();
}
