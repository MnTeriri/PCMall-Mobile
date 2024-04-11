package com.example.pcmall.service;

import com.example.pcmall.model.Address;
import com.example.pcmall.model.response.ResponseResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AddressService {
    @FormUrlEncoded
    @POST("searchAddressList")
    Observable<ResponseResult<List<Address>>> searchAddressList(@Field("uid") String uid);
}
