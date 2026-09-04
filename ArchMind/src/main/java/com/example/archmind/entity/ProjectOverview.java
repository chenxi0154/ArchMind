package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("project_overview")
@Data
public class ProjectOverview {
    @TableId
    private Long id;
    private Long projectId;
    private String projectType;
    private String summary;
    private String description;
    private String techStackJson;      // Response.techStack 序列化后存这里
    private String architectureJson;   // Response.architecture 序列化后存这里
    private String modulesJson;        // Response.modules 序列化后存这里
    private String rawResponse;        // LLM 返回结果序列化，调试用
    private String model;
    private LocalDateTime createTime;
}
