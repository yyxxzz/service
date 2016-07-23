package com.yoho.gateway.interceptor;

import com.google.common.collect.Lists;
import com.yoho.core.common.monitor.ThreadProfile;
import com.yoho.core.common.utils.HttpRequestUtils;
import com.yoho.error.event.GatewayAccessEvent;
import com.yoho.gateway.utils.ServletUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 访问统计.
 * Created by chunhua.zhang@yoho.cn on 2015/12/9.
 */
public class AccessStatistics implements HandlerInterceptor, ApplicationEventPublisherAware {

    //记录开始时间
    private final static ThreadLocal<Long> start = new ThreadLocal<>();

    //publisher
    private ApplicationEventPublisher publisher;

    /**
     * 请求URL："POST /gateway HTTP/1.0" 200 98
     * GET /gateway/operations/api/v5/resource/home?app_version=3.9&client_secret=6482050a08c1b8d22c4d2e4f20780c59&client_type=android&content_code=9aa25f5
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        start.set(System.currentTimeMillis());

        ThreadProfile.enter(ServletUtils.getServiceName(request), "");
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            //publish event
            long startTime = start.get();
            long costTime = System.currentTimeMillis() - startTime;
            this.publisher.publishEvent(new GatewayAccessEvent(ServletUtils.getServiceName(request), response.getStatus(),
                    costTime, this.getLogReqParams(request,response)));

        } catch (Exception e) {
            //do nothing
        } finally {

            //remove thread local
            start.remove();
            ThreadProfile.exit();
        }
    }


    /**
     *  对一些关键的请求，需要记录请求参数, 获取失败的响应，也需要记录参数
     */
    private  Map<String, Object>  getLogReqParams(final HttpServletRequest request, final HttpServletResponse response){
        final List<String>  logMethods = Lists.newArrayList("app.Shopping.submit");
        Map<String, Object> args = null;
        if(response.getStatus() >= 400 || logMethods.contains(ServletUtils.getServiceName(request))){
            args = HttpRequestUtils.getRequestParams(request);
        }
        return  args;
    }



    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


}
