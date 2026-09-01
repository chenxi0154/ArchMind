package com.example.archmind.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadResponse {
//项目资源ID
    private Long sourceId;
//项目ID
    private Long projectId;
//文件名
    private String fileName;
//文件大小
    private Long  fileSize;
//上传状态
    private String status;
//解析任务ID
    private Long taskId;
}

