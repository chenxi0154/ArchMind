package com.example.archmind.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 认证相关 1000-1999
    UNAUTHORIZED(1001, "未登录或Token已过期"),
    FORBIDDEN(1002, "无权限访问"),
    TOKEN_INVALID(1003, "Token无效"),
    TOKEN_EXPIRED(1004, "Token已过期"),

    // 用户相关 2000-2999
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_EXIST(2002, "用户名已存在"),
    USER_PASSWORD_ERROR(2003, "用户名或密码错误"),
    USER_LOCKED(2004, "账号已被锁定"),

    // 参数相关 3000-3999
    PARAM_ERROR(3001, "参数错误"),
    PARAM_MISSING(3002, "参数缺失"),
    PARAM_FORMAT_ERROR(3003, "参数格式错误"),

    // 业务相关 4000-4999
    BUSINESS_ERROR(4000, "业务异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
