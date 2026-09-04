package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.dto.response.FileTreeNode;
import com.example.archmind.dto.response.UploadResponse;
import com.example.archmind.service.FileTreeNodeService;
import com.example.archmind.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectSourceController {
    private final UploadService uploadService;
    private final FileTreeNodeService fileTreeNodeService;

    @PostMapping("/{projectId}/upload")
    public Result<UploadResponse> upload(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        return Result.success(uploadService.upload(projectId, file));
    }

    @GetMapping("/{projectId}/tree")
    public Result<List<FileTreeNode>> treeNode(@PathVariable Long projectId,
                                               @RequestParam(required = false) Long parentId) {
        return Result.success(fileTreeNodeService.listChildren(projectId, parentId));
    }

}
