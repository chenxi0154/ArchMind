package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("entity_code_mapper")
public class EntityCodeMapper {
    @TableId
    private Long id;
    private Long entityId;
    private Long codeElementId;
    private String relationType;
}
