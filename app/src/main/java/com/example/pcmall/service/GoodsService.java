package com.example.pcmall.service;

import com.example.pcmall.model.Goods;
import com.example.pcmall.model.response.ResponseResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GoodsService {
    @FormUrlEncoded
    @POST("searchGoodsList")
    Observable<ResponseResult<List<Goods>>> searchGoodsList(
            @Field("currentPage") Integer currentPage,
            @Field("pageSize") Integer pageSize);

    @POST("searchTotalCount")
    Observable<ResponseResult<Long>> searchTotalCount();


}
