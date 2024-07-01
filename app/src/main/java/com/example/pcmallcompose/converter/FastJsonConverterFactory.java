package com.example.pcmallcompose.converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FastJsonConverterFactory extends Converter.Factory {
    private static ScalarsConverterFactory scalarsConverterFactory;

    public static FastJsonConverterFactory create() {
        scalarsConverterFactory = ScalarsConverterFactory.create();
        return new FastJsonConverterFactory();
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NonNull Type type, @NonNull Annotation[] parameterAnnotations, @NonNull Annotation[] methodAnnotations, @NonNull Retrofit retrofit) {
        return new FastJsonRequestBodyConverter<>();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        Converter<ResponseBody, ?> responseBodyConverter = scalarsConverterFactory.responseBodyConverter(type, annotations, retrofit);
        if (responseBodyConverter != null) {
            return responseBodyConverter;
        }
        return new FastJsonResponseBodyConverter<>(type);
    }


}
