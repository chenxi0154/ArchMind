package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {
    @TableId
    private Long id;
    private Long projectId;
    private Long userId;
    private String type;
    private String status;
    private Integer progress;
    private String currentStage;
    private String detail;
    private String error;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
