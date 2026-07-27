package com.example.archmind.controller;

import com.example.archmind.common.result.Result;
import com.example.archmind.dto.request.LoginRequest;
import com.example.archmind.dto.request.RegisterRequest;
import com.example.archmind.dto.response.LoginResponse;
import com.example.archmind.dto.response.UserInfoResponse;
import com.example.archmind.entity.User;
import com.example.archmind.service.AuthService;
import com.example.archmind.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


import org.springframework.web.bind.annotation.*;

@RestController
//身份标签
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;
    private AuthService authService;

    @PostMapping("/register")
    public Result<UserInfoResponse> register(@Valid@RequestBody RegisterRequest request){

        User user =userService.UserRegister(request);

        UserInfoResponse response = UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createTime(user.getCreateTime())
                .build();


        return Result.success(response);
    }

    @PostMapping("/login")
    public Result<LoginResponse> Login(@Valid@RequestBody LoginRequest request, HttpServletRequest httpSRequest){
        String clientIp = getClientIp(httpSRequest);
        LoginResponse response = authService.login(request,clientIp);

        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<LoginResponse> Logout(@RequestHeader("Authorization") String token){
        authService.logout(token);
       return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@RequestHeader("Authorization") String refreshToken){
       LoginResponse  response = authService.refreshToken(refreshToken);
       return Result.success(response);
    }

    private String getClientIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
        }
    }

