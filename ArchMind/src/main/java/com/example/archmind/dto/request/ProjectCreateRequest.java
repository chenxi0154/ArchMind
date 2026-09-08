package com.example.archmind.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectCreateRequest {

    @NotBlank(message = "项目名称不能为空")
    @Size(min = 1, max = 50, message = "项目名称长度需在 1-50 之间")
    private String name;

    @Size(max = 200, message = "项目描述不能超过 200 字")
    private String description;

    private String gitUrl;
}
