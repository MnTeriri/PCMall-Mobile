package com.example.pcmall.service;

import com.example.pcmall.model.response.ResponseResult;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OrderService {
    @FormUrlEncoded
    @POST("getRecordsFiltered")
    Observable<ResponseResult<Long>> getRecordsFiltered(
            @Field("uid") String uid,
            @Field("type") Integer type);
}
