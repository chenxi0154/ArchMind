package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("code_element")
public class CodeElement {
    @TableId
    private Long id;
    private Long projectId;
    private Long fileId;
    private Long parentId;
    private String elementType;
    private String name;
    private String qualifiedName;
    private String signature;
    private Integer startLine;
    private Integer endLine;
    private String visibility;
    private String metadataJson;
}
