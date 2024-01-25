package com.example.pcmall.service;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginService {
    @FormUrlEncoded
    @POST("login")
    Observable<String> login(@Field("uid") String uid, @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Call<String> register(String uid, String password);

    @GET("captcha.jpg")
    Observable<String> getCaptcha();
}
