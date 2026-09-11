package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.util.CheckProjectUtil;
import com.example.archmind.dto.response.TaskResponse;
import com.example.archmind.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务查询：提交在 ProjectAnalysisController，这里只管"查进度"。
 */
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CheckProjectUtil checkProjectUtil;

    @GetMapping("/{taskId}")
    public Result<TaskResponse> get(@PathVariable Long taskId) {
        return Result.success(taskService.get(taskId, checkProjectUtil.getCurrentUserId()));
    }
}
