package com.yoho.gateway.utils;

import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  utils
 * Created by chunhua.zhang@yoho.cn on 2016/3/24.
 */
public class ServletUtils {


    public final static String getServiceName(HttpServletRequest request) {
        //如果直接请求/gateway 或者/，则取method请求参数作为method
        final List<String> CONTEXTS = Lists.newArrayList("/", "/gateway", "/gateway/");
        String requestUrl = request.getRequestURI();
        String service = requestUrl;
        if (CONTEXTS.contains(service)) {
            service = request.getParameter("method");
        }
        return service;
    }

    public final static Map<String, String> getRequestParams(HttpServletRequest httpServletRequest) {
            Map<String, String> map = new HashMap<>();
            Enumeration paramNames = httpServletRequest.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String key = (String) paramNames.nextElement();
                String value = httpServletRequest.getParameter(key);
                map.put(key, value);
            }
            return map;
    }

}
