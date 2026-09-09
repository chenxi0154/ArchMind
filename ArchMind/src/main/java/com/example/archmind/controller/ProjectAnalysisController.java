package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.util.CheckProjectUtil;
import com.example.archmind.dto.response.ProjectOverviewResponse;
import com.example.archmind.service.ProjectOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectAnalysisController {

    private final ProjectOverviewService projectOverviewService;
    private final CheckProjectUtil checkProjectUtil;
    @PostMapping("/{projectId}/analyze")
    public Result<ProjectOverviewResponse> projectOverviewResponseResult(@PathVariable Long projectId,
                                                                         @RequestParam(defaultValue = "false") boolean force) {

        checkProjectUtil.checkProject(projectId);
//        1.实现project概况（force=true 时强制重新分析并覆盖；默认已有结果则直接返回）
        return Result.success(projectOverviewService.projectOverview(projectId, force));
    }

    @GetMapping("/{projectId}/overview")
    public Result<ProjectOverviewResponse> overview(@PathVariable Long projectId) {
        checkProjectUtil.checkProject(projectId);
        return Result.success(projectOverviewService.projectDatabaseOverview(projectId));
    }
}
