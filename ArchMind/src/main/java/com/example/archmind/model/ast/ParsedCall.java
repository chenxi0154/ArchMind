package com.example.archmind.model.ast;

/**
 * 一条悬空调用记录——A 阶段从方法体抽出，C 阶段拿索引定名。
 * receiver 可能是变量名("userService")、类名("UserService")、"this"、或复杂表达式(a.b())，
 * 也可能为 null（裸调用 foo()）。C 阶段无法消解的一律丢弃。
 */
public record ParsedCall(
        String receiver,
        String methodName,
        int argCount,
        int line
) {}
