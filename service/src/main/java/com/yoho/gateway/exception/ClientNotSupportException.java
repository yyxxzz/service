package com.yoho.gateway.exception;

/**
 *  客户端不受支持异常
 * Created by chzhang@yoho.cn on 2015/11/5.
 */
public class ClientNotSupportException extends  GatewayException {

    public ClientNotSupportException() {
        super(500, "不接受此平台");
    }

}

