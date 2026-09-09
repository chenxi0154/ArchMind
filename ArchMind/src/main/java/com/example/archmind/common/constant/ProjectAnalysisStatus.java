package com.example.archmind.common.constant;

/**
 * 项目分析状态（project.analysis_status 列取值）。
 */
public final class ProjectAnalysisStatus {

    private ProjectAnalysisStatus() {
    }

    /** 已创建，但尚未上传源码 */
    public static final String NO_SOURCE = "NO_SOURCE";

    /** 已上传源码、待分析 */
    public static final String PENDING = "PENDING";

    /** 分析成功 */
    public static final String COMPLETED = "COMPLETED";

    /** 分析失败，可重新分析 */
    public static final String FAILED = "FAILED";
}
