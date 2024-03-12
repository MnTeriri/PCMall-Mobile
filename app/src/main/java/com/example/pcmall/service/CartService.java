package com.example.pcmall.service;

import com.example.pcmall.model.Cart;
import com.example.pcmall.model.response.ResponseResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CartService {
    @FormUrlEncoded
    @POST("searchCartList")
    Observable<ResponseResult<List<Cart>>> searchCartList(
            @Field("uid") String uid,
            @Field("currentPage") Integer currentPage,
            @Field("pageSize") Integer pageSize);

    @FormUrlEncoded
    @POST("getTotalCount")
    Observable<ResponseResult<Long>> getTotalCount(@Field("uid") String uid);
}
