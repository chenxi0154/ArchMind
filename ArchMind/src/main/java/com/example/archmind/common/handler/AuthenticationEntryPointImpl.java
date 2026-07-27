package com.example.archmind.common.handler;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 未认证处理器
 * 处理未登录或 Token 无效的情况
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private  ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.warn("未认证访问：{}，错误信息：{}", request.getRequestURI(), authException.getMessage());

        // 设置响应格式
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 构建统一响应体
        Result<Void> result = Result.fail(ResultCode.UNAUTHORIZED);

        // 写入响应
        objectMapper.writeValue(response.getOutputStream(), result);
    }
}