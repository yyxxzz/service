package com.yoho.gateway.exception;

/**
 * client_secutity 不匹配
 *
 * Created by chzhang@yoho.cn on 2015/11/5.
 */
public class SecurityNotMatchException extends  GatewayException {

    /**
     * 异常
     *
     */
    public SecurityNotMatchException() {
        super(500, "数据验证错误.");
    }

}
