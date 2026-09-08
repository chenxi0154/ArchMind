package com.example.archmind.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`file`")
public class FileEntity {
    @TableId
    private Long id;
    private Long projectId;
    private Long parentId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String language;
    private Boolean hasChildren;
    private String hash;
    private LocalDateTime createTime;
}
