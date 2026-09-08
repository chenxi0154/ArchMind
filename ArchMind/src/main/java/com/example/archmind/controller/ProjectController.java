package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.util.CheckProjectUtil;
import com.example.archmind.dto.request.ProjectCreateRequest;
import com.example.archmind.dto.response.ProjectResponse;
import com.example.archmind.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final CheckProjectUtil checkProjectUtil;

    @PostMapping
    public Result<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        return Result.success(projectService.createProject(checkProjectUtil.getCurrentUserId(), request));
    }

    @GetMapping
    public Result<List<ProjectResponse>> myProjects() {
        return Result.success(projectService.listMyProjects(checkProjectUtil.getCurrentUserId()));
    }
}
