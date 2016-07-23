package com.yoho.gateway.exception;

/**
 * Created by xjipeng on 16/2/15.
 */
public class SessionExpireException extends  GatewayException {

    public SessionExpireException() {
        super(401, "登录会话超时,请退出重新登录.");
    }

}
