package com.example.pcmallcompose.model.response

enum class ResponseCode(val code: Int? = null, val message: String? = null) {
    OK(200, "操作成功"),
    ERROR(100000, "操作失败"),
    NO_TOKEN_ERROR(500000, "没有token"),
    TOKEN_EXPIRE_ERROR(500001, "token过期"),
    CAPTCHA_ERROR(500002, "验证码错误"),
    ACCOUNT_ERROR(500003, "账号或密码错误"),
    USER_EXIST_ERROR(500004, "账号存在"),
    ENTITY_NOT_FOUND(500005, "查询信息失败"),
    GOODS_NOT_ENOUGH_ERROR(500006, "商品缺货"),
    GOODS_OFF_SHELF_ERROR(500007, "商品下架"),
    CART_MIN_COUNT_ERROR(500008, "购物车数量最小"),
    CART_GOODS_ERROR(500009, "购物车商品状态异常"),
    CART_EMPTY_ERROR(500010, "购物车为空"),
    AUTHORIZED_ERROR(401, "没有权限，需要登录"),
    FORBIDDEN_ERROR(403, "权限不够，被拒绝"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");
}