package com.test.common.result;

/**
 * @author by Lixq
 * @Classname ResultCode
 * @Description TODO
 * @Date 2021/4/3 22:21
 */
public enum ResultCode implements IErrorCode{

    SUCCESS("200", "操作成功"),
    FAILED("500", "操作失败"),
    EXCEPTION("999", "系统异常"),
    VALIDATE_FAILED("500", "参数检验失败"),
    UNAUTHORIZED("404", "暂未登录或token已经过期"),
    FORBIDDEN("403", "没有相关权限");
    private String code;
    private String message;

    private ResultCode(String code, String message) {
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
