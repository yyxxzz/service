package com.yoho.gateway.exception;

import com.yoho.error.GatewayError;

/**
 *
 *  API Gateway异常父类
 *
 * Created by chang@yoho.cn on 2015/11/3.
 */
public class GatewayException extends  Exception{

    private int code;
    private String desc;

    /**
     *
     * 异常
     * @param gatewayError gateway错误码
     */
    public GatewayException(GatewayError gatewayError){
        this.code = gatewayError.getCode();
        this.desc = gatewayError.getMessage();
    }


    /**
     */
    public GatewayException(int code, String desc){
       this.code = code;
       this.desc=desc;

    }

    @Override
    public String getMessage() {
        return "[" + this.code + ":" + this.desc + "]";
    }
    public String getDesc() {
        return desc;
    }
    public int getErrorCode() {
        return code;
    }
}
