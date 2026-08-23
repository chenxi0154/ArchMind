package com.example.archmind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "upload")
@Component
public class UploadProperties {
//    代码上传后统一落地的根目录
    private String baseDir = "/data/code-workspace";
//    压缩包大小限制，单位为MB
    private long maxZipSizeMb = 500;
//    git 超时时间，单位为秒
    private int gitTimeOutSeconds = 60;

}
