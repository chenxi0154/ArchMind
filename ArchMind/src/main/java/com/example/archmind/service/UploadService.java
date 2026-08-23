package com.example.archmind.service;

import com.example.archmind.dto.request.GitUploadRequest;
import com.example.archmind.dto.response.UploadResponse;

public interface UploadService {
    UploadResponse upload(GitUploadRequest request);
}
