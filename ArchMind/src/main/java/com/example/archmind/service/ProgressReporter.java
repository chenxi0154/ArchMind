package com.example.archmind.service;

/**
 * 进度回调：业务阶段用它上报"阶段内进度"，但不关心这个数字最终写到哪里。
 * 由调用方（AnalysisTaskExecutor）决定写入方式（当前是写 Redis）。
 */
@FunctionalInterface
public interface ProgressReporter {
    /** @param percentWithinStage 阶段内进度 0~100 */
    void report(int percentWithinStage);
}
