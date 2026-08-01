package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("business_domain")
public class BusinessDomain {
    @TableId
    private Long id;
    private Long projectId;
    private Long candidateId;
    private String name;
    private String description;
    private String businessGoal;
    private String boundary;
    private String importance;
    private String analysisResultJson;
}
