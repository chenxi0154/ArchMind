package com.example.archmind.model.ast;

/**
 * 类字段：名称 + 原始类型写法。
 * 字段不建 Neo4j 节点，但有两个用途：
 *   1. 补进作用域类型表（让 "userService.save()" 能认出收者类型）
 *   2. 字段类型在项目内时产一条 DEPENDS 边
 */
public record ParsedField(
        String name,
        String type,
        String visibility,
        boolean isStatic,
        int line
) {}
