package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("entity_domain")
public class EntityDomain {
    @TableId
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private String tableName;
    private String entityType;
}
