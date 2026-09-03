package com.example.archmind.service;

import com.example.archmind.dto.response.FileTreeNode;

import java.util.List;

public interface FileTreeNodeService {
    List<FileTreeNode> listChildren(Long projectId, Long parentId);
}
