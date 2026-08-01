package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("code_relation")
public class CodeRelation {
    @TableId
    private Long id;
    private Long projectId;
    private Long sourceElementId;
    private Long targetElementId;
    private String relationType;
}
