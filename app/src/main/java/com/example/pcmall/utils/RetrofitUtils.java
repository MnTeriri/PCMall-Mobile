package com.example.pcmall.utils;

import com.alibaba.fastjson2.JSON;
import com.example.pcmall.model.response.ResponseResult;

import java.io.IOException;

import retrofit2.HttpException;

public class RetrofitUtils {
    public static <T> ResponseResult<T> getErrorMessage(Throwable throwable) throws IOException {
        ResponseResult<T> responseResult = new ResponseResult<>();
        if (throwable instanceof HttpException) {
            String errorMessage = "";
            HttpException httpException = (HttpException) throwable;
            errorMessage = httpException.response().errorBody().string();
            responseResult = JSON.parseObject(errorMessage, ResponseResult.class);
        }
        return responseResult;
    }
}
