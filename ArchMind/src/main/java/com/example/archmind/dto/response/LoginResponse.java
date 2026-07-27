package com.example.archmind.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long userId;
    private String username;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
