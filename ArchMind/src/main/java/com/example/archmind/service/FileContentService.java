package com.example.archmind.service;

import java.nio.file.Path;

/**
 * 文件内容读取服务：判断文件是否为文本/二进制，并在安全与大小限制下读取文本内容。
 */
public interface FileContentService {

    /**
     * 读取文本文件内容（UTF-8）。
     * 文件不存在、不可读、是二进制文件或超过大小限制时会抛出 {@code BusinessException}。
     *
     * @param filePath 文件绝对路径
     * @return 文件文本内容
     */
    String readTextContent(Path filePath);

    /**
     * 判断是否为文本文件。
     */
    boolean isTextFile(Path filePath);

    /**
     * 判断是否为二进制文件。
     */
    boolean isBinaryFile(Path filePath);

    /**
     * 判断文件是否允许被读取（存在、是普通文件、可读、非符号链接）。
     */
    boolean isAllowedToRead(Path filePath);
}
