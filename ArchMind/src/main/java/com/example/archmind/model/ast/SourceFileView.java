package com.example.archmind.model.ast;

/**
 * 一个待解析文件的完整输入：fileId + 相对路径 + 源码文本。
 * 解析器只认 content，不碰磁盘——这样喂字符串就能单测。
 */
public record SourceFileView(
        Long fileId,
        String relativePath,
        String content
) {}
