package com.yoho.gateway.controller.exception;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.yoho.core.common.utils.HttpRequestUtils;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.error.exception.handler.ServiceGlobalExceptionHandler;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.exception.SessionExpireException;
import com.yoho.gateway.utils.ServletUtils;

/**
 * Gateway全局异常处理。
 *
 * 如果是 #{@link ServiceException} 或者 #{@link GatewayException}, 则返回200，并且返回json消息体
 *
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler  {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {

        //用户未登录,或登录 会话超时
        final String serviceName = ServletUtils.getServiceName(request);

        if( e instanceof SessionExpireException){
            log.info("session expire at url:{}, params:{} exception is:{}", serviceName, HttpRequestUtils.getRequestParams(request), e);
            response.setStatus(401);
            return new ModelAndView();
        }

        //返回200
        if (e instanceof GatewayException || e instanceof ServiceException) {
            int code;
            String desc;
            if (e instanceof GatewayException) {
                code = ((GatewayException) e).getErrorCode();
                desc = ((GatewayException) e).getDesc();
            } else {  //服务异常，不能直接返回给客户端，必须映射一下
                ServiceException serviceException = (ServiceException) e;
                ServiceError serviceError = serviceException.getServiceError();
                code = serviceError.getMappingGatewayError().getLeft();
                desc = serviceError.getMappingGatewayError().getRight();
                if (serviceException.getParams() != null) {
                    desc = MessageFormat.format(desc, serviceException.getParams());
                }
            }

            log.info("service exception happened at:{}. code:{} desc:{}", serviceName, code,desc);
            ModelAndView mv = ServiceGlobalExceptionHandler.getErrorJsonView(code, desc);
            return mv;
        }

        log.warn("spring mvc exception at url:{}, params:{} exception is:{}.", serviceName,  HttpRequestUtils.getRequestParams(request), ExceptionUtils.getStackTrace(e));
        response.setStatus(500);
        return new ModelAndView();
    }

}