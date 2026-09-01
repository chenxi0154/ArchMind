package com.example.archmind.service.impl;

import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.common.security.SecurityUser;
import com.example.archmind.config.UploadProperties;
import com.example.archmind.dao.ProjectMapper;
import com.example.archmind.dao.ProjectSourceMapper;
import com.example.archmind.dto.response.UploadResponse;
import com.example.archmind.entity.FileEntity;
import com.example.archmind.entity.Project;
import com.example.archmind.entity.ProjectSource;
import com.example.archmind.service.FileScannerService;
import com.example.archmind.service.UploadService;
import com.example.archmind.service.ZipExtractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 上传服务实现：校验 -> 落盘 -> 建 source 记录 -> 解压 -> 扫描入库。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService {

    private static final String SOURCE_TYPE_ZIP = "zip";
    private static final String STATUS_UPLOADING = "UPLOADING";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_FAILED = "FAILED";

    private final UploadProperties uploadProperties;
    private final ProjectMapper projectMapper;
    private final ProjectSourceMapper projectSourceMapper;
    private final ZipExtractService zipExtractService;
    private final FileScannerService fileScannerService;

    @Override
    public UploadResponse upload(Long projectId, MultipartFile file) {
        // 1. 安全基础校验
        validateFile(file);
        // 2. 校验项目存在且属于当前用户
        Project project = checkProject(projectId);

        // 3. 先创建 project_source 记录，用其 ID 作为落盘目录名
        ProjectSource source = createSource(projectId, file.getOriginalFilename());

        try {
            // 4. 创建项目资源目录并保存 ZIP
            Path sourceDir = resolveSourceDir(projectId, source.getId());
            Files.createDirectories(sourceDir);

            Path zipPath = sourceDir.resolve(safeFileName(file.getOriginalFilename()));
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 5. 解压到 source 子目录
            Path extractDir = sourceDir.resolve("source");
            zipExtractService.extract(zipPath, extractDir);

            // 6. 扫描目录，将文件与目录写入 file 表
            List<FileEntity> files = fileScannerService.scan(extractDir, projectId, null);
            Long rootFileId = files.isEmpty() ? null : files.get(0).getId();

            // 7. 回填 project_source
            source.setContent(extractDir.toString());
            source.setFileId(rootFileId);
            source.setAnalysisStatus(STATUS_PENDING);
            projectSourceMapper.updateById(source);

            return UploadResponse.builder()
                    .sourceId(source.getId())
                    .projectId(projectId)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .status(STATUS_PENDING)
                    .taskId(null)
                    .build();
        } catch (BusinessException e) {
            markFailed(source);
            throw e;
        } catch (Exception e) {
            markFailed(source);
            log.error("上传处理失败 projectId={}, sourceId={}", projectId, source.getId(), e);
            throw new BusinessException("上传处理失败: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件为空");
        }

        long maxBytes = uploadProperties.getMaxZipSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BusinessException("压缩包大小超过限制: " + uploadProperties.getMaxZipSizeMb() + "MB");
        }

        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".zip")) {
            throw new BusinessException("仅支持上传 .zip 压缩包");
        }

        // 校验 ZIP 魔数（PK 开头），防止仅改了后缀的非压缩文件
        try (InputStream in = file.getInputStream()) {
            byte[] head = in.readNBytes(4);
            if (head.length < 2 || head[0] != 'P' || head[1] != 'K') {
                throw new BusinessException("文件不是有效的 zip 压缩包");
            }
        } catch (IOException e) {
            throw new BusinessException("读取上传文件失败");
        }
    }

    private Project checkProject(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目 ID 不能为空");
        }
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在: " + projectId);
        }
        checkOwnership(project);
        return project;
    }

    private void checkOwnership(Project project) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            Long userId = securityUser.getUserId();
            if (userId != null && project.getUserId() != null && !userId.equals(project.getUserId())) {
                throw new BusinessException("无权操作该项目");
            }
        }
    }

    private ProjectSource createSource(Long projectId, String fileName) {
        ProjectSource source = new ProjectSource();
        source.setProjectId(projectId);
        source.setSourceType(SOURCE_TYPE_ZIP);
        source.setContent(fileName);
        source.setAnalysisStatus(STATUS_UPLOADING);
        source.setCreateTime(LocalDateTime.now());
        projectSourceMapper.insert(source);
        return source;
    }

    private void markFailed(ProjectSource source) {
        try {
            source.setAnalysisStatus(STATUS_FAILED);
            projectSourceMapper.updateById(source);
        } catch (Exception e) {
            log.error("更新 project_source 失败状态失败, sourceId={}", source.getId(), e);
        }
    }

    private Path resolveSourceDir(Long projectId, Long sourceId) {
        return Paths.get(uploadProperties.getBaseDir())
                .resolve(String.valueOf(projectId))
                .resolve(String.valueOf(sourceId))
                .toAbsolutePath()
                .normalize();
    }

    private String safeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "upload.zip";
        }
        String base = name.replace('\\', '/');
        int idx = base.lastIndexOf('/');
        if (idx >= 0) {
            base = base.substring(idx + 1);
        }
        if (base.isBlank() || ".".equals(base) || "..".equals(base)) {
            return "upload.zip";
        }
        return base;
    }
}
