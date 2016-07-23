package com.yoho.gateway.controller.order.payment.common;

import com.yoho.core.common.utils.YHMath;

/**
 * Created by ming on 15/12/3.
 * Ref. YOHOPay_Package_Response
 */
public class PayResult {

    /**
     *  支付平台ID
     */
    private byte paymentID;

    /**
     *  支付结果,0为成功,1为失败
     */
    private int paymentResult;

    /**
     * 付款时间
     */
    private String paymentTime;

    /**
     * 订单Code
     */
    private String orderCode;

    /**
     * 支付的总金额
     */
    private double totalFee; // 为了与Orders类的Amount属性兼容

    /**
     * 银行名称,中文
     */
    private String bankName;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 结果信息
     */
    private String resultMsg;

    /**
     * 支付平台接受的订单号
     */
    private String payOrderCode;

    /**

     * 支付平台流水号
     */
    private String tradeNo;

    /**
     * 银行的流水号
     */
    private String bankBillNo;

    /**
     * uid
     * @return
     */
    private int uid;

    private int orderType;

    public String getCallbackTime() {
        return callbackTime;
    }

    public void setCallbackTime(String callbackTime) {
        this.callbackTime = callbackTime;
    }

    /**
     * 回调时间
     * @return
     */
    private String callbackTime;


    public String getBankBillNo() {
        return bankBillNo;
    }

    public void setBankBillNo(String bankBillNo) {
        this.bankBillNo = bankBillNo;
    }

    public byte getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(byte paymentID) {
        this.paymentID = paymentID;
    }

    public int getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(int paymentResult) {
        this.paymentResult = paymentResult;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFeeInYuan(double totalFee) {
        this.totalFee = totalFee;
    }

    public void setTotalFeeInCent(double totalFeeInFen) {
        this.totalFee = YHMath.mul(totalFeeInFen, 0.01);
    }
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getPayOrderCode() {
        return payOrderCode;
    }

    public void setPayOrderCode(String payOrderCode) {
        this.payOrderCode = payOrderCode;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public int getUid() { return uid; }

    public void setUid(int uid) { this.uid = uid; }

    public int getOrderType() { return orderType; }

    public void setOrderType(int orderType) { this.orderType = orderType; }

}
