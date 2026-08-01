package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledgeObject")
public class KnowledgeObject {
    @TableId
    private Long id;
//    节点名称
    private String name;
//      类型
    private String type;
//    节点描述
    private String overview;
//    AI生成解析
    private String aiSummary;
//    创建时间
    private LocalDateTime createTime;


}
