package com.example.archmind.service.impl;

import com.example.archmind.dto.request.GitUploadRequest;
import com.example.archmind.dto.response.UploadResponse;
import com.example.archmind.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    @Override
    public  UploadResponse upload(GitUploadRequest request){

        return upload(request);
    }
}
