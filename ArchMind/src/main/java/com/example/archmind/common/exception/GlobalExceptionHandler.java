package com.example.archmind.common.exception;

import com.example.archmind.common.result.Result;
import com.example.archmind.common.result.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

        /**
         * 处理业务异常
         */
        @ExceptionHandler(BusinessException.class)
        public Result<Void> handleBusinessException(BusinessException e) {
            log.warn("业务异常: {}", e.getMessage());
            return Result.fail(e.getCode(), e.getMessage());
        }

        /**
         * 处理 @Valid 校验异常（RequestBody）
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
            String message = e.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            log.warn("参数校验失败: {}", message);
            return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
        }

        /**
         * 处理 @Validated 校验异常（参数绑定）
         */
        @ExceptionHandler(BindException.class)
        public Result<Void> handleBindException(BindException e) {
            String message = e.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            log.warn("参数绑定失败: {}", message);
            return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
        }

        /**
         * 处理约束校验异常
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
            String message = e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            log.warn("约束校验失败: {}", message);
            return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
        }

        /**
         * 处理其他未捕获异常
         */
        @ExceptionHandler(Exception.class)
        public Result<Void> handleException(Exception e) {
            log.error("系统异常: ", e);
            return Result.fail(ResultCode.FAIL.getCode(), "系统内部错误，请稍后重试");
        }
}
