package com.example.archmind.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data

public class UploadResponse {

    private String name;

    private LocalDateTime upLoad_time;

}
