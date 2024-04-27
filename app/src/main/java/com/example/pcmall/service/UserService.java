package com.example.pcmall.service;

import com.example.pcmall.model.User;
import com.example.pcmall.model.response.ResponseResult;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface UserService {
    @POST("updateInformation")
    Observable<ResponseResult<User>> updateInformation(@Body User user);
}
