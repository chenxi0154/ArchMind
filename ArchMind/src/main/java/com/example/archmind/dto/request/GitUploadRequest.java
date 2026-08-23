package com.example.archmind.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.aop.framework.AopInfrastructureBean;

import java.time.LocalDateTime;

@Data
public class GitUploadRequest {
    @NotBlank
    private String repoUrl;
    private String branch = "main";
    private String authType = "none";
    private String username;
    private String password;
}
