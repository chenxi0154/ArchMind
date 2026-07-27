package com.example.archmind.service;

import com.example.archmind.dto.request.LoginRequest;
import com.example.archmind.dto.response.LoginResponse;
import com.example.archmind.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {


    LoginResponse login(LoginRequest request, String clientIp);

    void logout(String token);

    LoginResponse refreshToken(String refreshToken);

    boolean validateTokenFromRedis(String token);

    Long getUserIdFromToken(String token);
}
