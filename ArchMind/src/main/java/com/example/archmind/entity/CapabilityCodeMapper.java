package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("capability_code_mapper")
public class CapabilityCodeMapper {
    @TableId
    private Long id;
    private Long capabilityId;
    private Long codeElementId;
    private String relationType;
}
