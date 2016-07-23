package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

/**
 * Created by zhouxiang on 2016/4/7.
 */
public class SellerApplyLogReqVO extends BaseBO {

    private static final long serialVersionUID = 3346881507270458727L;

    private String brandName;

    private String registerStatus;

    private String sellerName;

    private String sellerAddress;

    private String zipCode;

    private String contacts;

    private String contactPhone;

    private String contactEmail;

    private String onlineShopWebsite;

    private String categoryInfo;

    private String storeInfo;

    private String sellerRole;

    private String brandWebsite;

    private String billingCycle;

    private String warehouseAddress;

    private String producer;

    private String invoiceType;

    private String newCycle;

    private int quarterNum;

    private String supplyCycle;

    private String haveStore;

    private String brandMaterial;

    private String goodsMaterial;

    private int uid;

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getOnlineShopWebsite() {
        return onlineShopWebsite;
    }

    public void setOnlineShopWebsite(String onlineShopWebsite) {
        this.onlineShopWebsite = onlineShopWebsite == null ? null : onlineShopWebsite.trim();
    }

    public String getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(String categoryInfo) {
        this.categoryInfo = categoryInfo == null ? null : categoryInfo.trim();
    }

    public String getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(String storeInfo) {
        this.storeInfo = storeInfo == null ? null : storeInfo.trim();
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public String getRegisterStatus() {
        return registerStatus;
    }

    public void setRegisterStatus(String registerStatus) {
        this.registerStatus = registerStatus == null ? null : registerStatus.trim();
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName == null ? null : sellerName.trim();
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress == null ? null : sellerAddress.trim();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode == null ? null : zipCode.trim();
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts == null ? null : contacts.trim();
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone == null ? null : contactPhone.trim();
    }

    public String getSellerRole() {
        return sellerRole;
    }

    public void setSellerRole(String sellerRole) {
        this.sellerRole = sellerRole == null ? null : sellerRole.trim();
    }

    public String getBrandWebsite() {
        return brandWebsite;
    }

    public void setBrandWebsite(String brandWebsite) {
        this.brandWebsite = brandWebsite == null ? null : brandWebsite.trim();
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle == null ? null : billingCycle.trim();
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress == null ? null : warehouseAddress.trim();
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer == null ? null : producer.trim();
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType == null ? null : invoiceType.trim();
    }

    public String getNewCycle() {
        return newCycle;
    }

    public void setNewCycle(String newCycle) {
        this.newCycle = newCycle == null ? null : newCycle.trim();
    }

    public int getQuarterNum() {
        return quarterNum;
    }

    public void setQuarterNum(int quarterNum) {
        this.quarterNum = quarterNum;
    }

    public String getSupplyCycle() {
        return supplyCycle;
    }

    public void setSupplyCycle(String supplyCycle) {
        this.supplyCycle = supplyCycle == null ? null : supplyCycle.trim();
    }

    public String getHaveStore() {
        return haveStore;
    }

    public void setHaveStore(String haveStore) {
        this.haveStore = haveStore == null ? null : haveStore.trim();
    }

    public String getBrandMaterial() {
        return brandMaterial;
    }

    public void setBrandMaterial(String brandMaterial) {
        this.brandMaterial = brandMaterial == null ? null : brandMaterial.trim();
    }

    public String getGoodsMaterial() {
        return goodsMaterial;
    }

    public void setGoodsMaterial(String goodsMaterial) {
        this.goodsMaterial = goodsMaterial == null ? null : goodsMaterial.trim();
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

}
