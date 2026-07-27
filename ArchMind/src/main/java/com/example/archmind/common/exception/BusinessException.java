package com.example.archmind.common.exception;

import com.example.archmind.common.result.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
        private final Integer code;

        public BusinessException(String message){
            super(message);
            this.code = ResultCode.BUSINESS_ERROR.getCode();
        }
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}
