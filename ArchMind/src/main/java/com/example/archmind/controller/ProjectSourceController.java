package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.dto.response.UploadResponse;
import com.example.archmind.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectSourceController {
    private final UploadService uploadService;

    @PostMapping("/{projectId}/upload")
    public Result<UploadResponse> upload(@PathVariable Long projectId, @RequestParam("file")MultipartFile file){
        return Result.success(uploadService.upload(projectId, file));
    }
}
