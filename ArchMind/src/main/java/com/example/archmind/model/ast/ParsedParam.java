package com.example.archmind.model.ast;

/**
 * 方法参数：名称 + 原始类型写法（短名，如 "UserService"）。
 * 用于拼签名、补作用域类型表。
 */
public record ParsedParam(
        String name,
        String type
) {}
