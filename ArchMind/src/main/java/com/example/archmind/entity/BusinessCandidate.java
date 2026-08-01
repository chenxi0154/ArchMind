package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("business_candidate")
public class BusinessCandidate {
    @TableId
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private String source;
    private BigDecimal confidence;
    private Long fileCount;
    private Long elementCount;
    private String status;
}
