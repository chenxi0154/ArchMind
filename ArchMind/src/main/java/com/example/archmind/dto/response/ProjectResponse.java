package com.example.archmind.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String language;
    private String framework;
    private String gitUrl;
    private String version;
    private String status;
    private LocalDateTime createTime;
}
