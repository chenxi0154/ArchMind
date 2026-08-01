package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_analysis_task")
public class AiAnalysisTask {
    @TableId
    private Long id;
    private Long projectId;
    private String analysisType;
    private String targetType;
    private Long targetId;
    private String prompt;
    private String response;
    private String model;
    private String status;
    private LocalDateTime createTime;
}
