package com.example.pcmall.service;

import com.example.pcmall.model.Category;
import com.example.pcmall.model.response.ResponseResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.POST;

public interface CategoryService {
    @POST("searchCategoryList")
    Observable<ResponseResult<List<Category>>> searchCategoryList();
}
