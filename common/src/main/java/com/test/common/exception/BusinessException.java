package com.test.common.exception;

import com.test.common.result.IErrorCode;

/**
 * @author by Lixq
 * @Classname BusinessException
 * @Description TODO
 * @Date 2021/4/3 22:38
 */
public class BusinessException extends RuntimeException {

    private IErrorCode errorCode;

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
