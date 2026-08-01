package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("capability")
public class Capability {
    @TableId
    private Long id;
    private Long businessDomainId;
    private String name;
    private String description;
    private String businessValue;
    private String input;
    private String output;
    private String status;
    private String analysisJson;
}
