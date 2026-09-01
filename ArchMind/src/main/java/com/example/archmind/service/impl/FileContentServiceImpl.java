package com.example.archmind.service.impl;

import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.service.FileContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Set;

/**
 * 文件内容读取实现。
 *
 * <p>文本/二进制判定采用「扩展名优先 + 未知扩展名 NUL 字节采样」的策略，
 * 读取文本内容时对大小做上限保护，避免大文件一次性读入内存。</p>
 */
@Service
@Slf4j
public class FileContentServiceImpl implements FileContentService {

    /** 单次允许读取全文的最大字节数（10MB）。 */
    private static final long MAX_READ_BYTES = 10L * 1024 * 1024;

    /** 二进制检测的采样字节数。 */
    private static final int SAMPLE_SIZE = 8192;

    /** 明确的二进制扩展名。 */
    private static final Set<String> BINARY_EXTENSIONS = Set.of(
            "zip", "jar", "war", "ear", "tar", "gz", "tgz", "bz2", "xz", "7z", "rar",
            "png", "jpg", "jpeg", "gif", "bmp", "ico", "webp", "tif", "tiff", "psd",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "class", "exe", "dll", "so", "dylib", "o", "a", "lib", "pyc", "pyo",
            "bin", "dat", "db", "sqlite", "sqlite3", "mp3", "mp4", "avi", "mov", "mkv",
            "wav", "flac", "ogg", "ttf", "otf", "woff", "woff2", "eot", "svgz", "wasm"
    );

    /** 明确的文本扩展名。 */
    private static final Set<String> TEXT_EXTENSIONS = Set.of(
            "java", "txt", "md", "markdown", "yml", "yaml", "xml", "json", "properties",
            "sql", "js", "jsx", "ts", "tsx", "css", "scss", "less", "html", "htm", "vue",
            "go", "py", "rb", "php", "c", "h", "cpp", "hpp", "cc", "hh", "cs", "sh", "bash",
            "bat", "cmd", "ps1", "kt", "swift", "rs", "scala", "groovy", "gradle", "toml",
            "ini", "cfg", "conf", "csv", "tsv", "log", "env", "proto", "thrift", "lua",
            "r", "pl", "pm", "asm", "s", "vim", "editorconfig", "license", "gitignore"
    );

    @Override
    public String readTextContent(Path filePath) {
        if (!isAllowedToRead(filePath)) {
            throw new BusinessException("文件不存在或不可读: " + filePath);
        }
        if (isBinaryFile(filePath)) {
            throw new BusinessException("二进制文件不支持读取文本内容: " + filePath);
        }

        long size = fileSize(filePath);
        if (size > MAX_READ_BYTES) {
            throw new BusinessException("文件过大，无法直接读取全文: " + size
                    + " bytes（上限 " + MAX_READ_BYTES + " bytes）");
        }

        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BusinessException("读取文件内容失败: " + filePath);
        }
    }

    @Override
    public boolean isTextFile(Path filePath) {
        return isAllowedToRead(filePath) && !isBinaryFile(filePath);
    }

    @Override
    public boolean isBinaryFile(Path filePath) {
        if (!isAllowedToRead(filePath)) {
            return false;
        }

        String ext = extensionOf(filePath);
        if (BINARY_EXTENSIONS.contains(ext)) {
            return true;
        }
        if (TEXT_EXTENSIONS.contains(ext)) {
            return false;
        }

        // 未知扩展名：采样检测是否包含 NUL 字节
        try (InputStream in = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[SAMPLE_SIZE];
            int read = in.read(buffer);
            for (int i = 0; i < read; i++) {
                if (buffer[i] == 0) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            log.warn("二进制检测失败，按文本处理: {}", filePath, e);
            return false;
        }
    }

    @Override
    public boolean isAllowedToRead(Path filePath) {
        if (filePath == null) {
            return false;
        }
        // NOFOLLOW_LINKS：拒绝符号链接，避免读取到目录树之外的内容
        return Files.isRegularFile(filePath, LinkOption.NOFOLLOW_LINKS)
                && Files.isReadable(filePath);
    }

    private String extensionOf(Path filePath) {
        Path name = filePath.getFileName();
        if (name == null) {
            return "";
        }
        String fileName = name.toString();
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase();
    }

    private long fileSize(Path filePath) {
        try {
            return Files.size(filePath);
        } catch (IOException e) {
            return 0L;
        }
    }
}
