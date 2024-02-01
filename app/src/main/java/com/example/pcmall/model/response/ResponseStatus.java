package com.example.pcmall.model.response;

import lombok.Getter;

@Getter
public enum ResponseStatus {
    OK(200, "操作成功"),
    NO_TOKEN_ERROR(500000, "没有token"),
    TOKEN_EXPIRE_ERROR(500001, "token过期"),
    CAPTCHA_ERROR(500002, "验证码错误"),
    ACCOUNT_ERROR(500003, "账号或密码错误"),
    AUTHORIZED_ERROR(401, "没有权限，需要登录"),
    FORBIDDEN_ERROR(403, "权限不够，被拒绝");

    private final Integer code;
    private final String message;

    ResponseStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
