package com.example.archmind.service.impl;

import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.dao.FileEntityMapper;
import com.example.archmind.entity.FileEntity;
import com.example.archmind.service.FileScannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/**
 * 项目文件扫描实现：递归遍历解压目录，将目录与文件写入 file 表，并建立父子关系。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileScannerServiceImpl implements FileScannerService {

    /** 超过该大小的文件跳过 SHA-256 计算，避免扫描超大仓库时耗时过长。 */
    private static final long MAX_HASH_BYTES = 100L * 1024 * 1024;

    /** 扩展名 -> 语言 映射。 */
    private static final Map<String, String> EXTENSION_LANGUAGE = Map.ofEntries(
            Map.entry("java", "Java"),
            Map.entry("kt", "Kotlin"),
            Map.entry("xml", "XML"),
            Map.entry("json", "JSON"),
            Map.entry("yml", "YAML"),
            Map.entry("yaml", "YAML"),
            Map.entry("properties", "Properties"),
            Map.entry("sql", "SQL"),
            Map.entry("md", "Markdown"),
            Map.entry("txt", "Text"),
            Map.entry("js", "JavaScript"),
            Map.entry("jsx", "JavaScript"),
            Map.entry("ts", "TypeScript"),
            Map.entry("tsx", "TypeScript"),
            Map.entry("css", "CSS"),
            Map.entry("html", "HTML"),
            Map.entry("htm", "HTML"),
            Map.entry("vue", "Vue"),
            Map.entry("py", "Python"),
            Map.entry("go", "Go"),
            Map.entry("rb", "Ruby"),
            Map.entry("php", "PHP"),
            Map.entry("c", "C"),
            Map.entry("h", "C"),
            Map.entry("cpp", "C++"),
            Map.entry("hpp", "C++"),
            Map.entry("cs", "C#"),
            Map.entry("sh", "Shell"),
            Map.entry("bash", "Shell"),
            Map.entry("bat", "Batch"),
            Map.entry("cmd", "Batch"),
            Map.entry("ps1", "PowerShell"),
            Map.entry("swift", "Swift"),
            Map.entry("rs", "Rust"),
            Map.entry("scala", "Scala"),
            Map.entry("groovy", "Groovy"),
            Map.entry("gradle", "Groovy"),
            Map.entry("toml", "TOML"),
            Map.entry("ini", "INI"),
            Map.entry("proto", "Protobuf"),
            Map.entry("thrift", "Thrift"),
            Map.entry("lua", "Lua"),
            Map.entry("r", "R"),
            Map.entry("pl", "Perl"),
            Map.entry("csv", "CSV"),
            Map.entry("tsv", "TSV")
    );

    private final FileEntityMapper fileEntityMapper;

    @Override
    public List<FileEntity> scan(Path rootDir, Long projectId, Long rootParentId) {
        if (rootDir == null || !Files.isDirectory(rootDir)) {
            throw new BusinessException("扫描目录不存在: " + rootDir);
        }
        List<FileEntity> result = new ArrayList<>();
        scanNode(rootDir, projectId, rootParentId, rootDir, result);
        return result;
    }

    private void scanNode(Path node, Long projectId, Long parentId, Path rootDir, List<FileEntity> result) {
        FileEntity entity = buildEntity(node, projectId, parentId, rootDir);
        fileEntityMapper.insert(entity);
        result.add(entity);

        if (Files.isDirectory(node, LinkOption.NOFOLLOW_LINKS)) {
            for (Path child : listChildren(node)) {
                scanNode(child, projectId, entity.getId(), rootDir, result);
            }
        }
    }

    private FileEntity buildEntity(Path node, Long projectId, Long parentId, Path rootDir) {
        boolean isDir = Files.isDirectory(node, LinkOption.NOFOLLOW_LINKS);

        FileEntity entity = new FileEntity();
        entity.setProjectId(projectId);
        entity.setParentId(parentId);
        entity.setFileName(fileNameOf(node));
        entity.setFilePath(toUnixPath(rootDir.relativize(node).toString()));
        entity.setFileType(isDir ? "dir" : "file");
        entity.setFileSize(isDir ? 0L : fileSize(node));
        entity.setLanguage(isDir ? null : detectLanguage(node));
        entity.setHash(isDir ? null : computeHash(node));
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    private List<Path> listChildren(Path dir) {
        List<Path> children = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                children.add(path);
            }
        } catch (IOException e) {
            log.warn("读取目录失败: {}", dir, e);
        }
        children.sort(Comparator.comparing(this::fileNameOf));
        return children;
    }

    private String fileNameOf(Path path) {
        Path name = path.getFileName();
        return name == null ? path.toString() : name.toString();
    }

    private String toUnixPath(String path) {
        return path.replace('\\', '/');
    }

    private long fileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            log.warn("读取文件大小失败: {}", path, e);
            return 0L;
        }
    }

    private String detectLanguage(Path path) {
        String fileName = fileNameOf(path);
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return null;
        }
        return EXTENSION_LANGUAGE.get(fileName.substring(dot + 1).toLowerCase());
    }

    private String computeHash(Path path) {
        try {
            long size = Files.size(path);
            if (size > MAX_HASH_BYTES) {
                log.debug("文件过大，跳过 SHA-256 计算: {}", path);
                return null;
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream in = Files.newInputStream(path)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            log.warn("计算文件 SHA-256 失败: {}", path, e);
            return null;
        }
    }
}
