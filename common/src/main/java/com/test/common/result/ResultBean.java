package com.test.common.result;

import lombok.Data;

/**
 * @author by Lixq
 * @Classname ResultBean
 * @Description TODO
 * @Date 2021/4/3 22:13
 */
@Data
public class ResultBean<T> {

    public static final String CODE_TAG = "code";

    public static final String MSG_TAG = "msg";

    public static final String DATA_TAG = "data";

    private static final long serialVersionUID = 1L;

    private String code;

    private String msg;

    private T data;

    public ResultBean() {}

    protected ResultBean(String code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     */
    public static <T> ResultBean<T> success() {
        return new ResultBean<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(),null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> ResultBean<T> success(T data) {
        return new ResultBean<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }


    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> ResultBean<T> success(T data, String message) {
        return new ResultBean<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> ResultBean<T> failed(IErrorCode errorCode) {
        return new ResultBean<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }


    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> ResultBean<T> failed(ResultCode errorCode,String message) {
        return new ResultBean<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> ResultBean<T> failed(String message) {
        return new ResultBean<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> ResultBean<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> ResultBean<T> validateFailed(String message) {
        return new ResultBean<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }


    /*public static ResultBean error(ErrorInfo error) {
        return new ResultBean(error.getErrorCode(), error.getErrorMsg());
    }

    public static ResultBean error(ErrorInfoDistribution error) {
        return new ResultBean(error.getErrorCode(), error.getErrorMsg());
    }

    public static ResultBean error(ErrorResponse errorResponse) {
        return new ResultBean(errorResponse.getCode(), errorResponse.getMsg());
    }*/


}

