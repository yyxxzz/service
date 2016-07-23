package com.yoho.gateway.model.bigdata.vo;

/**
 * Created by yoho on 2016/5/27.
 */
public class ManagementKpiResponseVO {

    private String dateId;

    private String shopId;

    private String shopName;

    private String brandId;

    private String brandName;

    private String buyNumbers;

    private String orderAmount;

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public String getBuyNumbers() {
        return buyNumbers;
    }

    public void setBuyNumbers(String buyNumbers) {
        this.buyNumbers = buyNumbers;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    @Override
    public String toString() {
        return "ManagementKpiResponseVO{" +
                "dateId='" + dateId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", brandId='" + brandId + '\'' +
                ", brandName='" + brandName + '\'' +
                ", buyNumbers='" + buyNumbers + '\'' +
                ", orderAmount='" + orderAmount + '\'' +
                '}';
    }
}
