package com.example.archmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.common.util.CheckProjectUtil;
import com.example.archmind.dao.FileEntityMapper;
import com.example.archmind.dao.ProjectSourceMapper;
import com.example.archmind.dto.response.FileTreeNode;
import com.example.archmind.entity.FileEntity;
import com.example.archmind.entity.ProjectSource;
import com.example.archmind.service.FileTreeNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileTreeNodeServiceImpl implements FileTreeNodeService {

    private final CheckProjectUtil checkProjectUtil;
    private final FileEntityMapper fileEntityMapper;
    private final ProjectSourceMapper projectSourceMapper;

    @Override
    public List<FileTreeNode> listChildren(Long projectId, Long parentId) {
        checkProjectUtil.checkProject(projectId);

        Long actualParentId = resolveParentId(projectId, parentId);
        if (actualParentId == null) {
            return List.of();
        }

        List<FileEntity> children = fileEntityMapper.selectList(
                new LambdaQueryWrapper<FileEntity>()
                        .eq(FileEntity::getProjectId, projectId)
                        .eq(FileEntity::getParentId, actualParentId));

        children.sort(Comparator
                .comparing((FileEntity f) -> "dir".equals(f.getFileType()) ? 0 : 1)
                .thenComparing(FileEntity::getFileName, Comparator.nullsFirst(String::compareTo)));

        return children.stream().map(this::toTreeNode).toList();

    }

    /**
     * 定位实际要查的父节点：parentId 为空/0 时用 project_source.fileId 定位根目录，
     * 否则校验该节点存在且属于当前项目。
     */
    private Long resolveParentId(Long projectId, Long parentId) {
        if (parentId == null || parentId == 0L) {
            List<ProjectSource> sources = projectSourceMapper.selectList(
                    new LambdaQueryWrapper<ProjectSource>()
                            .eq(ProjectSource::getProjectId, projectId));
            return sources.stream()
                    .map(ProjectSource::getFileId)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        FileEntity parent = fileEntityMapper.selectById(parentId);
        if (parent == null || !projectId.equals(parent.getProjectId())) {
            throw new BusinessException("该父节点不存在或不属于该项目");
        }
        return parentId;
    }

    private FileTreeNode toTreeNode(FileEntity entity) {
        return FileTreeNode.builder()
                .fileId(entity.getId())
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .hasChildren(entity.getHasChildren())
                .fileSize(entity.getFileSize())
                .language(entity.getLanguage())
                .build();
    }
}
