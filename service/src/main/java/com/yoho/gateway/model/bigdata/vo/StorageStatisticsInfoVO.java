package com.yoho.gateway.model.bigdata.vo;

/**
 * Created by yoho on 2016/5/27.
 */
public class StorageStatisticsInfoVO {

    /**
     * 库存总数量
     */
    private String storageTotalNum;


    /**
     * 库存总金额（元）
     */
    private String storageTotalMoney;

    /**
     * 已上架商品数
     */
    private String onSaleTotalNum;

    /**
     * 待上架商品数
     */
    private String preSaleTotalNum;

    /**
     * 已下架商品数
     */
    private String outSaleTotalNum;


    public String getStorageTotalNum() {
        return storageTotalNum;
    }

    public void setStorageTotalNum(String storageTotalNum) {
        this.storageTotalNum = storageTotalNum;
    }

    public String getStorageTotalMoney() {
        return storageTotalMoney;
    }

    public void setStorageTotalMoney(String storageTotalMoney) {
        this.storageTotalMoney = storageTotalMoney;
    }

    public String getOnSaleTotalNum() {
        return onSaleTotalNum;
    }

    public void setOnSaleTotalNum(String onSaleTotalNum) {
        this.onSaleTotalNum = onSaleTotalNum;
    }

    public String getPreSaleTotalNum() {
        return preSaleTotalNum;
    }

    public void setPreSaleTotalNum(String preSaleTotalNum) {
        this.preSaleTotalNum = preSaleTotalNum;
    }

    public String getOutSaleTotalNum() {
        return outSaleTotalNum;
    }

    public void setOutSaleTotalNum(String outSaleTotalNum) {
        this.outSaleTotalNum = outSaleTotalNum;
    }

    @Override
    public String toString() {
        return "StorageStatisticsInfoVO{" +
                "storageTotalNum='" + storageTotalNum + '\'' +
                ", storageTotalMoney='" + storageTotalMoney + '\'' +
                ", onSaleTotalNum='" + onSaleTotalNum + '\'' +
                ", preSaleTotalNum='" + preSaleTotalNum + '\'' +
                ", outSaleTotalNum='" + outSaleTotalNum + '\'' +
                '}';
    }
}
