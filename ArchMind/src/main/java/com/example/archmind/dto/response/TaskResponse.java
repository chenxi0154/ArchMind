package com.example.archmind.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private Long taskId;          // 合成的"已完成"响应里为 null
    private Long projectId;
    private String type;          // ANALYSIS
    private String status;        // PENDING/RUNNING/SUCCESS/FAILED
    private Integer progress;     // 0-100
    private String currentStage;  // SCAN/AST/OVERVIEW
    private String stageLabel;    // 中文，给前端直接显示
    private Object detail;        // 终态时是概况结果；否则 null
    private String error;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
