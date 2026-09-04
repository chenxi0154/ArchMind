package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.util.CheckProjectUtil;
import com.example.archmind.dto.response.ProjectOverviewResponse;
import com.example.archmind.service.ProjectOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectAnalysisController {

    private final ProjectOverviewService projectOverviewService;
    private final CheckProjectUtil checkProjectUtil;
    @PostMapping("/{projectId}/analyze")
    public Result<ProjectOverviewResponse> projectOverviewResponseResult(@PathVariable Long projectId){

        checkProjectUtil.checkProject(projectId);
//        1.实现project概况
        return Result.success(projectOverviewService.projectOverview(projectId));
    }
}
