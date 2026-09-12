package com.example.archmind.model.ast;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个文件的解析结果。
 * packageName + imports 是 C 阶段消解短类名的关键上下文——
 * 同一个 "UserService"，靠这两个才能确定是哪个包的。
 */
@Data
public class ParsedFile {

    private Long fileId;

    private String relativePath;

    /** 包名，默认包为空串 */
    private String packageName;

    /** import 列表，如 ["com.x.UserService", "java.util.List"] */
    private List<String> imports = new ArrayList<>();

    /** 本文件所有类（含打平的嵌套类） */
    private List<ParsedClass> classes = new ArrayList<>();
}
