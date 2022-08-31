package com.test.common.interceptor.token;

import com.test.common.enums.CommonConstant;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 存放token到上下文供队列调用feign使用
 * @author
 */
public class TransmitUserTokenFilter implements Filter {

    public TransmitUserTokenFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.initUserInfo((HttpServletRequest) request);
        chain.doFilter(request, response);
    }

    private void initUserInfo(HttpServletRequest request) {
        String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        if (token!=null) {
            try {
                //将token放入上下文中
                UserTokenContext.setToken(token);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void destroy() {
    }
}