package com.example.pcmall.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseResult<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseResult<T> ok(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(ResponseStatus.OK.getCode());
        result.setMessage(ResponseStatus.OK.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> ok(T data, String msg) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(ResponseStatus.OK.getCode());
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    public static ResponseResult<String> error(ResponseStatus status) {
        return new ResponseResult<>(status.getCode(), status.getMessage(), null);
    }

    public static ResponseResult<String> error(Integer code, String msg) {
        return new ResponseResult<>(code, msg, null);
    }
}