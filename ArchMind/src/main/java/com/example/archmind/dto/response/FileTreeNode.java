package com.example.archmind.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileTreeNode {
    private Long fileId;
    private String fileName;
    private String fileType;
    private Boolean hasChildren;
    private Long fileSize;
    private String language;
}
