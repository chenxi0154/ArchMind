package com.example.archmind.service;

import com.example.archmind.entity.FileEntity;

import java.nio.file.Path;
import java.util.List;

/**
 * 项目文件扫描服务：递归遍历解压后的项目目录，识别目录与文件并写入 file 表。
 */
public interface FileScannerService {

    /**
     * 扫描目录树，将目录与文件逐条保存到 file 表。
     *
     * @param rootDir      要扫描的根目录（绝对路径）
     * @param projectId    所属项目 ID
     * @param rootParentId 根目录在 file 表中的父节点 ID（通常为 null）
     * @return 按遍历顺序返回所有已保存的 FileEntity（首元素为根目录）
     */
    List<FileEntity> scan(Path rootDir, Long projectId, Long rootParentId);
}
