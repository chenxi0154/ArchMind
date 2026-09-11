package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.util.CheckProjectUtil;
import com.example.archmind.dto.response.ProjectOverviewResponse;
import com.example.archmind.dto.response.TaskResponse;
import com.example.archmind.service.ProjectOverviewService;
import com.example.archmind.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectAnalysisController {

    private final ProjectOverviewService projectOverviewService;
    private final CheckProjectUtil checkProjectUtil;
    private final TaskService taskService;

    @PostMapping("/{projectId}/analyze")
    public Result<TaskResponse> projectOverviewResponseResult(@PathVariable Long projectId,
                                                              @RequestParam(defaultValue = "false") boolean force) {

        checkProjectUtil.checkProject(projectId);
        Long userId = checkProjectUtil.getCurrentUserId();
//        1.实现project概况（force=true 时强制重新分析并覆盖；默认已有结果则直接返回）
        return Result.success(taskService.submitAnalysis(projectId,userId,force));

    }

    @GetMapping("/{projectId}/overview")
    public Result<ProjectOverviewResponse> overview(@PathVariable Long projectId) {
        checkProjectUtil.checkProject(projectId);
        return Result.success(projectOverviewService.projectDatabaseOverview(projectId));
    }

    /** 项目最近一次分析任务：前端刷新/换页后用它找回还在跑的任务，避免进度条丢失 */
    @GetMapping("/{projectId}/tasks/latest")
    public Result<TaskResponse> latestTask(@PathVariable Long projectId) {
        checkProjectUtil.checkProject(projectId);
        return Result.success(taskService.getLatestByProject(projectId, checkProjectUtil.getCurrentUserId()));
    }
}
