package com.example.archmind.common.handler;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;



/**
 * 无权限处理器
 * 处理已登录但无权限访问的情况
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private  ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("无权限访问：{}，错误信息：{}", request.getRequestURI(), accessDeniedException.getMessage());

        // 设置响应格式
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 构建统一响应体
        Result<Void> result = Result.fail(ResultCode.FORBIDDEN);

        // 写入响应
        objectMapper.writeValue(response.getOutputStream(), result);
    }
}