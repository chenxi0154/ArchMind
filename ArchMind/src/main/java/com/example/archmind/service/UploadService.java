package com.example.archmind.service;

import com.example.archmind.dto.response.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    UploadResponse upload(Long projectId, MultipartFile file);
}
