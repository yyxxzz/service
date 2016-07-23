package com.yoho.gateway.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  获取客户端IP保存在thread local中。 调用方法：{@link RemoteIPInterceptor#getRemoteIP()}
 * Created by chzhang@yoho.cn on 2015/11/5.
 */
public class RemoteIPInterceptor implements HandlerInterceptor {

    private static  final ThreadLocal<String> localIp = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ip = httpServletRequest.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            ip = httpServletRequest.getRemoteAddr();
        }

        localIp.set(ip);
        return true;
    }


    /**
     *  获取客户端IP地址
     * @return  客户端请求IP地址
     */
    public static String getRemoteIP(){
        return localIp.get();
    }


    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
           localIp.remove();
    }

}
