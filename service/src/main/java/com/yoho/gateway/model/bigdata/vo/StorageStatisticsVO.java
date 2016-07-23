package com.yoho.gateway.model.bigdata.vo;

import com.yoho.gateway.model.PageRequestBase;

/**
 * Created by yoho on 2016/5/27.
 */
public class StorageStatisticsVO extends PageRequestBase {

    private String productSkn;

    private String factory_code;

    private String productSku;

    private String shopId;

    private String brandId;

    private String maxSortId;

    private String middleSortId;

    private String smallSortId;

    public String getProductSkn() {
        return productSkn;
    }

    public void setProductSkn(String productSkn) {
        this.productSkn = productSkn;
    }

    public String getFactory_code() {
        return factory_code;
    }

    public void setFactory_code(String factory_code) {
        this.factory_code = factory_code;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getMaxSortId() {
        return maxSortId;
    }

    public void setMaxSortId(String maxSortId) {
        this.maxSortId = maxSortId;
    }

    public String getMiddleSortId() {
        return middleSortId;
    }

    public void setMiddleSortId(String middleSortId) {
        this.middleSortId = middleSortId;
    }

    public String getSmallSortId() {
        return smallSortId;
    }

    public void setSmallSortId(String smallSortId) {
        this.smallSortId = smallSortId;
    }

    @Override
    public String toString() {
        return "StorageStatisticsVO{" +
                "productSkn='" + productSkn + '\'' +
                ", factory_code='" + factory_code + '\'' +
                ", productSku='" + productSku + '\'' +
                ", shopId='" + shopId + '\'' +
                ", brandId='" + brandId + '\'' +
                ", maxSortId='" + maxSortId + '\'' +
                ", middleSortId='" + middleSortId + '\'' +
                ", smallSortId='" + smallSortId + '\'' +
                '}';
    }
}
