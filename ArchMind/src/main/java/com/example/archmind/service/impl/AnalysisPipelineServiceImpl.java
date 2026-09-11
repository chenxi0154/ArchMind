package com.example.archmind.service.impl;

import com.example.archmind.dto.response.ProjectOverviewResponse;
import com.example.archmind.service.AnalysisPipelineService;
import com.example.archmind.service.ProgressReporter;
import com.example.archmind.service.ProjectOverviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 分析流水线实现：同步、无异步、不碰 task 表。
 * 每个阶段独立可测——传入一个假的 ProgressReporter 就能单跑。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisPipelineServiceImpl implements AnalysisPipelineService {

    private final ProjectOverviewService projectOverviewService;

    @Override
    public void scanStage(Long projectId, ProgressReporter reporter) {
        // P1：上传时已完成解压 + 文件树入库，这里只做占位。
        // 将来若要在此补扫/校验，按"第 i/N 个目录"调 reporter.report(...)
        reporter.report(100);
    }

    @Override
    public void astStage(Long projectId, ProgressReporter reporter) {
        // TODO P1 第二刀：列出 java 文件 → JavaParser 解析 → 写 MySQL + Neo4j
        // 形如：for (int i = 0; i < files.size(); i++) { ... reporter.report(i * 100 / files.size()); }
        reporter.report(100);
    }

    @Override
    public ProjectOverviewResponse overviewStage(Long projectId, ProgressReporter reporter) {
        reporter.report(0);
        ProjectOverviewResponse response = projectOverviewService.generateOverview(projectId);
        reporter.report(100);
        return response;
    }
}
