package com.yoho.gateway.model.order;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * sunjiexiang
 * Created by yoho on 2016/3/15.
 */
public class HistoryOrderVO {

    /**
     * 订单编号
     */
    @JSONField(name = "order_code")
    private String orderCode;

    /**
     * 订单金额
     */
    private String amount;

    /**
     * 支付方式
     */
    private String payment;

    /**
     * 下单时间
     */
    @JSONField(name = "order_time")
    private String orderTime;

    /**
     * 订单商品
     */
    @JSONField(name = "goods_data")
    private String goodsData;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(String goodsData) {
        this.goodsData = goodsData;
    }
}
