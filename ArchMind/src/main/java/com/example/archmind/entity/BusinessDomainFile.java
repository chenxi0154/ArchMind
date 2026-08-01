package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("business_domain_file")
public class BusinessDomainFile {
    @TableId
    private Long id;
    private Long businessDomainId;
    private Long fileId;
    private String relationType;
    private BigDecimal score;
}
