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

    @FormUrlEncoded
    @POST("addCartCount")
    public Observable<ResponseResult<String>> addCartCount(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("subCartCount")
    public Observable<ResponseResult<String>> subCartCount(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("selectCart")
    public Observable<ResponseResult<String>> selectCart(@Field("id") Integer id, @Field("isSelect") Integer isSelect);

    @FormUrlEncoded
    @POST("selectAllCart")
    public Observable<ResponseResult<String>> selectAllCart(@Field("uid") String uid, @Field("isSelect") Integer isSelect);

    @FormUrlEncoded
    @POST("addCart")
    public Observable<ResponseResult<String>> addCart(@Field("uid") String uid, @Field("gid") Integer gid);

    @FormUrlEncoded
    @POST("deleteCart")
    public Observable<ResponseResult<String>> deleteCart(@Field("id") Integer id);
}
