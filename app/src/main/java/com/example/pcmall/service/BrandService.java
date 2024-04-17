package com.example.pcmall.service;

import com.example.pcmall.model.Brand;
import com.example.pcmall.model.response.ResponseResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BrandService {
    @FormUrlEncoded
    @POST("searchBrandByCid")
    Observable<ResponseResult<List<Brand>>> searchBrandByCid(@Field("cid") Integer cid);
}
