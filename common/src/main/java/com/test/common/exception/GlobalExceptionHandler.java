package com.test.common.exception;

import com.test.common.result.ResultBean;
import com.test.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author by Lixq
 * @Classname GlobalExceptionHandler
 * @Description TODO
 * @Date 2021/4/3 22:05
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultBean businessExceptionHandle(BusinessException ex) {
        if (ex.getErrorCode() != null) {
            return ResultBean.failed(ex.getErrorCode());
        }
        return ResultBean.failed(ex.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultBean handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return ResultBean.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public ResultBean handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return ResultBean.validateFailed(message);
    }

    @ExceptionHandler(value =Exception.class)
    public ResultBean exceptionHandler(Exception e){
        log.error("exceptionMessage",e);
        return ResultBean.failed(ResultCode.EXCEPTION);
    }
}
