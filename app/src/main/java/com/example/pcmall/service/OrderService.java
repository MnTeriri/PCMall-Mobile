package com.example.pcmall.service;

import com.example.pcmall.model.Order;
import com.example.pcmall.model.response.ResponseResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OrderService {
    @FormUrlEncoded
    @POST("searchOrderList")
    Observable<ResponseResult<List<Order>>> searchOrderList(
            @Field("searchValue") String searchValue,
            @Field("uid") String uid,
            @Field("type") Integer type,
            @Field("currentPage") Integer currentPage,
            @Field("pageSize") Integer pageSize);

    @FormUrlEncoded
    @POST("getRecordsFiltered")
    Observable<ResponseResult<Long>> getRecordsFiltered(
            @Field("searchValue") String searchValue,
            @Field("uid") String uid,
            @Field("type") Integer type);

    @FormUrlEncoded
    @POST("createOrder")
    Observable<ResponseResult<String>> createOrder(@Field("uid") String uid, @Field("aid") Integer aid);

    @FormUrlEncoded
    @POST("payOrder")
    Observable<ResponseResult<String>> payOrder(@Field("oid") String oid);

    @FormUrlEncoded
    @POST("finishOrder")
    Observable<ResponseResult<String>> finishOrder(@Field("oid") String oid);

    @FormUrlEncoded
    @POST("cancelOrder")
    Observable<ResponseResult<String>> cancelOrder(@Field("oid") String oid);

    @FormUrlEncoded
    @POST("refundOrder")
    Observable<ResponseResult<String>> refundOrder(@Field("oid") String oid);
}
