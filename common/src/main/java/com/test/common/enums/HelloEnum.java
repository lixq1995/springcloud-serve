package com.test.common.enums;

import com.test.common.result.IErrorCode;

/**
 * @author by Lixq
 * @Classname HelloEnum
 * @Description TODO
 * @Date 2021/4/3 22:49
 */
public enum HelloEnum implements IErrorCode{
    EXCEPTION_ONE("500", "异常问题1，请输入大于3的数字"),
    EXCEPTION_TWO("500", "异常问题2");

    private String code;
    private String message;

    private HelloEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
