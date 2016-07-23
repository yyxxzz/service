package com.yoho.gateway.exception;


/**
 *  请求头不正确
 * Created by chzhang@yoho.cn on 2015/11/5.
 */
public class RequestHeaderInvalidateException extends  GatewayException {


    /**
     * 异常
     *
     */
    public RequestHeaderInvalidateException(String headerName) {
        super(500, "缺少"+headerName);
    }

}
