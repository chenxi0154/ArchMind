package com.example.archmind.service;

import com.example.archmind.dto.response.ProjectOverviewResponse;

public interface ProjectOverviewService {

    /** 只读：缓存 → 库，都没有返回 null（供提交前判断是否已有结果） */
    ProjectOverviewResponse tryGetExistingOverview(Long projectId);

    /** 生成概况：读 pom/README → 调 LLM → 落库 + 写缓存（不改分析状态，由 TaskService 统一维护） */
    ProjectOverviewResponse generateOverview(Long projectId);

    /** 只读：给前端 GET overview 用，未分析则抛异常 */
    ProjectOverviewResponse projectDatabaseOverview(Long projectId);
}
