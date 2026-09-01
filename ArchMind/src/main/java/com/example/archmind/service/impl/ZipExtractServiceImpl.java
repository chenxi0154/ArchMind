package com.example.archmind.service.impl;

import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.service.ZipExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 压缩包解压实现，内置 Zip Slip（路径穿越）防护。
 */
@Service
@Slf4j
public class ZipExtractServiceImpl implements ZipExtractService {

    @Override
    public Path extract(Path zipFile, Path targetDirectory) {
        if (zipFile == null || !Files.isRegularFile(zipFile)) {
            throw new BusinessException("压缩文件不存在: " + zipFile);
        }

        Path normalizedTarget = targetDirectory.toAbsolutePath().normalize();
        try {
            Files.createDirectories(normalizedTarget);
        } catch (IOException e) {
            log.error("创建解压目录失败: {}", normalizedTarget, e);
            throw new BusinessException("创建解压目录失败: " + normalizedTarget);
        }

        try (InputStream inputStream = Files.newInputStream(zipFile);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path entryPath = resolveEntry(normalizedTarget, entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Path parent = entryPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            log.error("解压失败: {} -> {}", zipFile, normalizedTarget, e);
            throw new BusinessException("解压失败: " + e.getMessage());
        }

        return normalizedTarget;
    }

    /**
     * 将压缩包内的条目名解析为解压目标路径，并阻止逃逸到目标目录之外。
     */
    private Path resolveEntry(Path targetDir, String entryName) throws IOException {
        Path resolved = targetDir.resolve(entryName).normalize();
        if (!resolved.startsWith(targetDir)) {
            throw new IOException("非法压缩条目，疑似 Zip Slip 攻击: " + entryName);
        }
        return resolved;
    }
}
