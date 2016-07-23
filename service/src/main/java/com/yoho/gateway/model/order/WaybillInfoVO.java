package com.yoho.gateway.model.order;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * qianjun
 * 2015/11/27
 */
public class WaybillInfoVO {
    private String acceptTime;

    @JSONField(name="accept_address")
    private String acceptAddress;

    @JSONField(name="express_id")
    private Byte expressId;

    @JSONField(name="express_number")
    private String expressNumber;

    @JSONField(name="order_code")
    private Long orderCode;

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptAddress() {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress) {
        this.acceptAddress = acceptAddress;
    }

    public Byte getExpressId() {
        return expressId;
    }

    public void setExpressId(Byte expressId) {
        this.expressId = expressId;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public Long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(Long orderCode) {
        this.orderCode = orderCode;
    }
}
