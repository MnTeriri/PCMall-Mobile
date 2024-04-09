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
            @Field("searchValue") String searchValue,
            @Field("currentPage") Integer currentPage,
            @Field("pageSize") Integer pageSize);

    @FormUrlEncoded
    @POST("getRecordsFiltered")
    Observable<ResponseResult<Long>> getRecordsFiltered(@Field("searchValue") String searchValue);


}
