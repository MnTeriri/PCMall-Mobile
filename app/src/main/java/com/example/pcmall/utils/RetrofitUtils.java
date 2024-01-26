package com.example.pcmall.utils;

import java.io.IOException;

import retrofit2.HttpException;

public class RetrofitUtils {
    public static String getErrorMessage(Throwable throwable) throws IOException {
        String errorMessage = "";
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            errorMessage = httpException.response().errorBody().string();
        }
        return errorMessage;
    }
}
