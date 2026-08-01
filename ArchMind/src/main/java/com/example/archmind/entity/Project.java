package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project {

    @TableId
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String language;
    private String framework;
    private String gitUrl;
    private String version;
    private String status;
    private LocalDateTime createTime;
}
