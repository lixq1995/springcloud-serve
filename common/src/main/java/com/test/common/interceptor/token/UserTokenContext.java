package com.test.common.interceptor.token;


import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 用户token上下文
 * @author
 */
public class UserTokenContext {

    private static ThreadLocal<String> userToken = new TransmittableThreadLocal<>();

    public UserTokenContext() {
    }

    public static String getToken(){
        return userToken.get();
    }

    public static void setToken(String token){
        userToken.set(token);
    }
}
