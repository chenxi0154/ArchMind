package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("project_dependency")
public class ProjectDependency {
    @TableId
    private Long id;
    private Long projectId;
    private String groupId;
    private String artifactId;
    private String version;
    private String type;
}
