package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_source")
public class ProjectSource {
    @TableId
    private Long id;
    private Long projectId;
    private String sourceType;
    private String content;
    private Long fileId;
    private String analysisStatus;
    private LocalDateTime createTime;
}
